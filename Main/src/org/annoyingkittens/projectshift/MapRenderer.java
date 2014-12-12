package org.annoyingkittens.projectshift;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

/**
 * Author: Bogdanov Kirill
 * Date: 01.09.12
 * Time: 19:36
 */
public class MapRenderer {
    int[][] tiles;
    Kyra kyra;
    public static OrthographicCamera cam;
    SpriteCache cache;
    SpriteBatch batch = new SpriteBatch(5000);
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    int[][] blocks;
    FPSLogger fps = new FPSLogger();
    private final static int VIEWPORT_WIDTH = 18;
    private final static int VIEWPORT_HEIGHT = 12;

    public MapRenderer(Map map) {
        tiles = map.getTiles();
        kyra = map.getKyra();
        cam = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        cam.position.set(kyra.getPosition().x, kyra.getPosition().y, 0);
        this.cache = new SpriteCache(tiles.length * tiles[0].length, false);
        this.blocks = new int[(int) Math.ceil(tiles.length / VIEWPORT_WIDTH)]
                [(int) Math.ceil(tiles[0].length / VIEWPORT_HEIGHT)];
        createBlocks();
    }

    private void createBlocks() {
        int width = tiles.length;
        int height = tiles[0].length;
        for (int blockY = 0; blockY < blocks[0].length; blockY++) {
            for (int blockX = 0; blockX < blocks.length; blockX++) {
                cache.beginCache();
                for (int y = blockY * VIEWPORT_HEIGHT; y < (blockY + 1) * VIEWPORT_HEIGHT; y++) {
                    for (int x = blockX * VIEWPORT_WIDTH; x < (blockX + 1) * VIEWPORT_WIDTH; x++) {
                        if (x > width) continue;
                        if (y > height) continue;
                        int posY = height - y - 1;
                        if (Map.match(tiles[x][y], Map.TILE) || Map.match(tiles[x][y], Map.LEDGEL) ||
                                Map.match(tiles[x][y], Map.LEDGER))
                            cache.add(Assets.tile, x, posY, 1, 1);
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
        Gdx.gl.glClearColor(1, 0.7f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.position.lerp(lerpTarget.set(kyra.getPosition().x, kyra.getPosition().y, 0), 2f * deltaTime);
        cam.update();
        cache.setProjectionMatrix(cam.combined);
        cache.begin();
        for (int blockY = 0; blockY < 4; blockY++) {
            for (int blockX = 0; blockX < 6; blockX++) {
                cache.draw(blocks[blockX][blockY]);
            }
        }
        cache.end();
        stateTime += deltaTime;
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        renderKyra();
        batch.draw(Assets.soundEnabled ? Assets.soundOn : Assets.soundOff, cam.position.x + 6, cam.position.y + 4, 1, 1);
        if (kyra.state == States.SHIFT) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 1, 0.2f);
            shapeRenderer.rect(0, 0, 720, 480);
            shapeRenderer.end();
        }
        batch.end();
        fps.log();
    }

    private void renderKyra() {
        /*Animation anim;
        boolean loop = true;
        if (kyra.state == States.RUN) {
            if (kyra.dir == Kyra.LEFT) {
                anim = Assets.kyraLeftRun;
            } else {
                anim = Assets.kyraRightRun;
            }
            batch.draw(anim.getKeyFrame(kyra.stateTime, loop), kyra.getPosition().x, kyra.getPosition().y, 1.67f, 2);
        }
        if (kyra.state == States.IDLE || kyra.state == States.SHIFT) {
            if (kyra.dir == Kyra.LEFT)
                anim = Assets.kyraIdleLeft;
            else
                anim = Assets.kyraIdleRight;
            batch.draw(anim.getKeyFrame(kyra.stateTime, loop), kyra.getPosition().x + 0.5f, kyra.getPosition().y, 0.32f, 2);
        }
        if (kyra.state == States.JUMP) {
            loop = false;
            if (kyra.dir == Kyra.LEFT)
                anim = Assets.kyraLeftJump;
            else
                anim = Assets.kyraRightJump;
            batch.draw(anim.getKeyFrame(kyra.stateTime, loop), kyra.getPosition().x, kyra.getPosition().y, 1.64f, 2.5f);
        }
        if (kyra.state == States.FALL) {
            loop = true;
            if (kyra.dir == Kyra.LEFT)
                anim = Assets.kyraLeftFall;
            else
                anim = Assets.kyraRightFall;
            batch.draw(anim.getKeyFrame(kyra.stateTime, loop), kyra.getPosition().x, kyra.getPosition().y, 1.16f, 2);
        }
        if (kyra.state == States.DROOP) {
            loop = false;
            if (kyra.dir == Kyra.LEFT)
                anim = Assets.kyraLeftDroop;
            else
                anim = Assets.kyraRightDroop;
            batch.draw(anim.getKeyFrame(kyra.stateTime, loop), kyra.getPosition().x, kyra.getPosition().y, 1.64f, 2.5f);
        }
        if (kyra.state == States.CLIMB) {
            loop = false;
            if (kyra.dir == Kyra.LEFT)
                anim = Assets.kyraLeftClimb;
            else
                anim = Assets.kyraRightClimb;
            batch.draw(anim.getKeyFrame(kyra.stateTime, loop), kyra.getPosition().x, kyra.getPosition().y, 0.82f, 2.5f);
        }*/
        TextureRegion texture;
        if (kyra.dir == Kyra.LEFT) {
            texture = Assets.kyraLeft;
        } else {
            texture = Assets.kyraRight;
        }
        batch.draw(texture, kyra.getPosition().x, kyra.getPosition().y, 1f, 2f);
    }

    public void dispose() {
        cache.dispose();
        batch.dispose();
    }
}
