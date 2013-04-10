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
    static OrthographicCamera cam;
    SpriteCache cache;
    SpriteBatch batch = new SpriteBatch(10000);
    int[][] blocks;
    TextureRegion tile;
    Animation kyraLeftRun;
    Animation kyraRightRun;
    Animation kyraIdleLeft;
    Animation kyraIdleRight;
    FPSLogger fps = new FPSLogger();
    Music shiftMusic;

    public MapRenderer(Map map) {
        this.map = map;
        cam = new OrthographicCamera(15, 10);
        cam.position.set(map.kyra.pos.x, map.kyra.pos.y, 0);
        this.cache = new SpriteCache(this.map.tiles.length * this.map.tiles[0].length, false);
        this.blocks = new int[(int) Math.ceil(this.map.tiles.length / 15.0f)][(int) Math.ceil(this.map.tiles[0].length / 10.0f)];
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
                for (int y = blockY * 10; y < blockY * 10 + 10; y++) {
                    for (int x = blockX * 15; x < blockX * 15 + 15; x++) {
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
        Texture kyraTexture = new Texture(Gdx.files.internal("data/kyra_run.png"));
        TextureRegion[][] rightRegion = new TextureRegion(kyraTexture).split(300, 360);
        TextureRegion[][] leftRegion = new TextureRegion(kyraTexture).split(300, 360);
        TextureRegion[] rightIdleRegion = new TextureRegion(kyraTexture).split(57, 360)[0];
        TextureRegion[] leftIdleRegion = new TextureRegion(kyraTexture).split(57, 360)[0];
        TextureRegion[] rightRun = new TextureRegion[30];
        TextureRegion[] leftRun = new TextureRegion[30];
        int count = 0;
        for (int i = 1; i < 4; i++) {
            for(int j = 0; j < 10; j++) {
                rightRun[count] = rightRegion[i][j];
                leftRegion[i][j].flip(true, false);
                leftRun[count++] = leftRegion[i][j];
            }
        }
        leftIdleRegion[0].flip(true, false);
        TextureRegion leftIdle = leftIdleRegion[0];
        TextureRegion rightIdle = rightIdleRegion[0];
        kyraIdleLeft = new Animation(0.5f, leftIdle);
        kyraIdleRight = new Animation(0.5f, rightIdle);
        kyraRightRun = new Animation(0.03f, rightRun);
        kyraLeftRun = new Animation(0.03f, leftRun);
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
        Animation anim;
        boolean loop = true;
        if (map.kyra.state == Kyra.RUN) {
            if (map.kyra.dir == Kyra.LEFT) {
                anim = kyraLeftRun;
            } else {
                anim = kyraRightRun;
            }
            batch.draw(anim.getKeyFrame(map.kyra.stateTime, loop), map.kyra.pos.x, map.kyra.pos.y, 1.67f, 2);
        }
        if (map.kyra.state == Kyra.IDLE || map.kyra.state == Kyra.SHIFT || map.kyra.state == Kyra.JUMP) {
            if (map.kyra.dir == Kyra.LEFT)
                anim = kyraIdleLeft;
            else
                anim = kyraIdleRight;
            batch.draw(anim.getKeyFrame(map.kyra.stateTime, loop), map.kyra.pos.x + 0.5f, map.kyra.pos.y, 0.32f, 2);
        }
    }


    public void dispose() {
        cache.dispose();
        batch.dispose();
        tile.getTexture().dispose();
        shiftMusic.dispose();
    }
}
