package com.projectshift;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

/**
 * Author: Bogdanov Kirill
 * Date: 01.09.12
 * Time: 19:36
 */
public class MapRenderer {
    Map map;
    OrthographicCamera cam;
    SpriteCache cache;
    SpriteBatch batch = new SpriteBatch(10000);
    int[][] blocks;
    TextureRegion tile;
    Animation kyraLeft;
    Animation kyraRight;
    Animation kyraIdleLeft;
    Animation kyraIdleRight;
    FPSLogger fps = new FPSLogger();
    Music shiftMusic;

    public MapRenderer(Map map) {
        this.map = map;
        this.cam = new OrthographicCamera(24, 16);
        this.cam.position.set(map.kyra.pos.x, map.kyra.pos.y, 0);
        this.cache = new SpriteCache(this.map.tiles.length * this.map.tiles[0].length, false);
        this.blocks = new int[(int) Math.ceil(this.map.tiles.length / 24.0f)][(int) Math.ceil(this.map.tiles[0].length / 16.0f)];
        shiftMusic = Gdx.audio.newMusic(Gdx.files.internal("C:/Users/Shepard/IdeaProjects/Project Shift/Main/src/data/Dev. Environment.mp3"));
        shiftMusic.setLooping(true);
        shiftMusic.play();
        createAnimations();
        createBlocks();
    }

    private void createBlocks() {
        int width = map.tiles.length;
        int height = map.tiles[0].length;
        for (int blockY = 0; blockY < blocks[0].length; blockY++) {
            for (int blockX = 0; blockX < blocks.length; blockX++) {
                cache.beginCache();
                for (int y = blockY * 16; y < blockY * 16 + 16; y++) {
                    for (int x = blockX * 24; x < blockX * 24 + 24; x++) {
                        if (x > width) continue;
                        if (y > height) continue;
                        int posY = height - y - 1;
                        if (map.match(map.tiles[x][y], Map.TILE)) cache.add(tile, x, posY, 1, 1);
                    }
                }
                blocks[blockX][blockY] = cache.endCache();
            }
        }
        Gdx.app.debug("Shift", "blocks created");
    }

    private void createAnimations() {
        this.tile = new TextureRegion(new Texture(Gdx.files.internal("data/tile.png")), 0, 0, 20, 20);
        Texture kyraTexture = new Texture(Gdx.files.internal("data/kyra.png"));
        TextureRegion[] split = new TextureRegion(kyraTexture).split(24, 45)[0];
        TextureRegion[] mirror = new TextureRegion(kyraTexture).split(24, 45)[0];
        for (TextureRegion region : mirror)
            region.flip(true, false);
        kyraRight = new Animation(0.1f, split[1], split[2], split[3], split[4], split[5]);
        kyraLeft = new Animation(0.1f, mirror[1], mirror[2], mirror[3], mirror[4], mirror[5]);
        kyraIdleRight = new Animation(0.5f, split[0]);
        kyraIdleLeft = new Animation(0.5f, mirror[0]);
    }

    float stateTime = 0;
    Vector3 lerpTarget = new Vector3();

    public void render(float deltaTime) {
        cam.position.lerp(lerpTarget.set(map.kyra.pos.x, map.kyra.pos.y, 0), 2f * deltaTime);
        cam.update();
        cache.setProjectionMatrix(cam.combined);
        Gdx.gl.glDisable(GL10.GL_BLEND);
        cache.begin();
        int b = 0;
        for (int blockY = 0; blockY < 4; blockY++) {
            for (int blockX = 0; blockX < 6; blockX++) {
                cache.draw(blocks[blockX][blockY]);
                b++;
            }
        }
        cache.end();
        Gdx.app.debug("Shift", "blocks: " + b);
        stateTime += deltaTime;
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        renderKyra();
        batch.end();
        fps.log();
    }

    private void renderKyra() {
        Animation anim = null;
        boolean loop = true;
        if (map.kyra.state == Kyra.RUN) {
            if (map.kyra.dir == Kyra.LEFT)
                anim = kyraLeft;
            else
                anim = kyraRight;
        }
        if (map.kyra.state == Kyra.IDLE) {
            if (map.kyra.dir == Kyra.LEFT)
                anim = kyraIdleLeft;
            else
                anim = kyraIdleRight;
        }
        if (anim != null) {
            batch.draw(anim.getKeyFrame(map.kyra.stateTime, loop), map.kyra.pos.x, map.kyra.pos.y, 1.2f, 2);
        }
    }

    public void dispose() {
        cache.dispose();
        batch.dispose();
        tile.getTexture().dispose();
        shiftMusic.dispose();
    }
}
