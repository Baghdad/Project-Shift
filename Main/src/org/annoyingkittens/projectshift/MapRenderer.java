package org.annoyingkittens.projectshift;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
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
    FPSLogger fps = new FPSLogger();

    public MapRenderer(Map map) {
        this.map = map;
        cam = new OrthographicCamera(15, 10);
        cam.position.set(map.kyra.pos.x, map.kyra.pos.y, 0);
        this.cache = new SpriteCache(this.map.tiles.length * this.map.tiles[0].length, false);
        this.blocks = new int[(int) Math.ceil(this.map.tiles.length / 15.0f)][(int) Math.ceil(this.map.tiles[0].length / 10.0f)];
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
                        if (map.match(map.tiles[x][y], Map.TILE)) cache.add(Assets.tile, x, posY, 1, 1);
                    }
                }
                blocks[blockX][blockY] = cache.endCache();
            }
        }
        Gdx.app.debug("Shift", "blocks created");
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
        batch.draw(Assets.soundEnabled ? Assets.soundOn : Assets.soundOff, cam.position.x + 6, cam.position.y + 4, 1, 1);
        batch.end();
        fps.log();
    }

    private void renderKyra() {
        Animation anim;
        boolean loop = true;
        if (map.kyra.state == States.RUN) {
            if (map.kyra.dir == Kyra.LEFT) {
                anim = Assets.kyraLeftRun;
            } else {
                anim = Assets.kyraRightRun;
            }
            batch.draw(anim.getKeyFrame(map.kyra.stateTime, loop), map.kyra.pos.x, map.kyra.pos.y, 1.67f, 2);
        }
        if (map.kyra.state == States.IDLE || map.kyra.state == States.SHIFT) {
            if (map.kyra.dir == Kyra.LEFT)
                anim = Assets.kyraIdleLeft;
            else
                anim = Assets.kyraIdleRight;
            batch.draw(anim.getKeyFrame(map.kyra.stateTime, loop), map.kyra.pos.x + 0.5f, map.kyra.pos.y, 0.32f, 2);
        }
        if (map.kyra.state == States.JUMP) {
            loop = false;
            if (map.kyra.dir == Kyra.LEFT)
                anim = Assets.kyraLeftJump;
            else
                anim = Assets.kyraRightJump;
            batch.draw(anim.getKeyFrame(map.kyra.stateTime, loop), map.kyra.pos.x, map.kyra.pos.y, 1.64f, 2.5f);
        }
        if (map.kyra.state == States.FALL) {
            loop = true;
            if (map.kyra.dir == Kyra.LEFT)
                anim = Assets.kyraLeftFall;
            else
                anim = Assets.kyraRightFall;
            batch.draw(anim.getKeyFrame(map.kyra.stateTime, loop), map.kyra.pos.x, map.kyra.pos.y, 1.16f, 2);
        }
    }


    public void dispose() {
        cache.dispose();
        batch.dispose();
    }
}
