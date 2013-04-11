package com.projectshift;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Author: Bogdanov Kirill
 * Date: 09.11.12
 * Time: 1:40
 */
public class Kyra {
    static final int IDLE = 0;
    static final int RUN = 1;
    static final int SPAWN = 2;
    static final int SHIFT = 3;
    static final int JUMP = 4;
    static final int RIGHT = 1;
    static final int LEFT = -1;
    static final float MAX_VEL = 3.5f;
    static final float JUMP_VELOCITY = 10;

    Vector2 pos = new Vector2();
    Vector2 vel = new Vector2();
    Vector2 accel = new Vector2();
    Vector3 touchPoint = new Vector3();
    public Rectangle bounds = new Rectangle();

    int state = SPAWN;
    float stateTime = 0;
    int dir = RIGHT;
    Map map;
    boolean grounded = false;

    public Kyra(Map map, float x, float y) {
        this.map = map;
        pos.x = x;
        pos.y = y;
        bounds.width = 1f;
        bounds.height = 2f;
        bounds.x = pos.x + 0.2f;
        bounds.y = pos.y;
        state = SPAWN;
        stateTime = 0;
    }

    public void update(float deltaTime) {
        processKeys();
        if (state != SHIFT) {
            accel.y = -10f;
        }
        accel.mul(deltaTime);
        vel.add(accel.x, accel.y);
        vel.mul(deltaTime);
        tryMove();
        vel.mul(1.0f / deltaTime);

        if (state == SPAWN) {
            if (stateTime > 0.4f) {
                state = IDLE;
            }
        }

        stateTime += deltaTime;
    }

    public void processKeys() {
        if (state == SPAWN) return;
        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && state != JUMP) {
            state = SHIFT;
            vel.x = 0;
            if (Gdx.input.justTouched()) {
                touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                MapRenderer.cam.unproject(touchPoint);
                bounds.x = touchPoint.x;
                bounds.y = touchPoint.y;
            }
        } else if (Gdx.input.isKeyPressed(Keys.W) && state != JUMP) {
            state = JUMP;
            vel.y = JUMP_VELOCITY;
            grounded = false;
        } else if (Gdx.input.isKeyPressed(Keys.D) && state != SHIFT) {
            if (state != JUMP) state = RUN;
            dir = RIGHT;
            vel.x = MAX_VEL * dir;
        } else if (Gdx.input.isKeyPressed(Keys.A) && state != SHIFT) {
            if (state != JUMP) state = RUN;
            dir = LEFT;
            vel.x = MAX_VEL * dir;
        } else {
            if (state != JUMP) state = IDLE;
            vel.x = 0;
        }
    }

    Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
    Rectangle[] sideRects = {new Rectangle(), new Rectangle()};

    private void tryMove() {
        bounds.x += vel.x;
        fetchCollidableRects();
        for (Rectangle rect : r) {
            if (bounds.overlaps(rect)) {
                if (vel.x < 0)
                    bounds.x = rect.x + rect.width + 0.01f;
                else
                    bounds.x = rect.x - bounds.width - 0.01f;
                vel.x = 0;
            }
        }

        for (Rectangle rect : sideRects) {
            if (bounds.overlaps(rect)) {
                if (vel.x < 0)
                    bounds.x = rect.x + rect.width + 0.01f;
                else
                    bounds.x = rect.x - bounds.width - 0.01f;
                vel.x = 0;
            }
        }

        bounds.y += vel.y;
        fetchCollidableRects();
        for (Rectangle rect : r) {
            if (bounds.overlaps(rect)) {
                if (vel.y < 0) {
                    bounds.y = rect.y + rect.height + 0.01f;
                    grounded = true;
                    if (state != SPAWN) state = Math.abs(vel.x) > 0.05f ? RUN : IDLE;
                } else
                    bounds.y = rect.y - bounds.height - 0.01f;
                vel.y = 0;
            }
        }

        pos.x = bounds.x - 0.2f;
        pos.y = bounds.y;
    }

    private void fetchCollidableRects() {
        int p1x = (int) bounds.x;
        int p1y = (int) Math.floor(bounds.y);
        int p2x = (int) (bounds.x + bounds.width);
        int p2y = (int) Math.floor(bounds.y);
        int p3x = (int) (bounds.x + bounds.width);
        int p3y = (int) (bounds.y + bounds.height);
        int p4x = (int) bounds.x;
        int p4y = (int) (bounds.y + bounds.height);
        int p5x = (int) bounds.x;
        int p5y = (int) (bounds.y + bounds.height / 2);
        int p6x = (int) (bounds.x + bounds.width);
        int p6y = (int) (bounds.y + bounds.height / 2);

        int[][] tiles = map.tiles;
        int tile1 = tiles[p1x][map.tiles[0].length - 1 - p1y];
        int tile2 = tiles[p2x][map.tiles[0].length - 1 - p2y];
        int tile3 = tiles[p3x][map.tiles[0].length - 1 - p3y];
        int tile4 = tiles[p4x][map.tiles[0].length - 1 - p4y];
        int tile5 = tiles[p5x][map.tiles[0].length - 1 - p5y];
        int tile6 = tiles[p6x][map.tiles[0].length - 1 - p6y];

        if (tile1 == Map.TILE)
            r[0].set(p1x, p1y, 1, 1);
        else
            r[0].set(-1, -1, 0, 0);
        if (tile2 == Map.TILE)
            r[1].set(p2x, p2y, 1, 1);
        else
            r[1].set(-1, -1, 0, 0);
        if (tile3 == Map.TILE)
            r[2].set(p3x, p3y, 1, 1);
        else
            r[2].set(-1, -1, 0, 0);
        if (tile4 == Map.TILE)
            r[3].set(p4x, p4y, 1, 1);
        else
            r[3].set(-1, -1, 0, 0);
        if (tile5 == Map.TILE)
            sideRects[0].set(p5x, p5y, 1, 1);
        else
            sideRects[0].set(-1, -1, 0, 0);
        if (tile6 == Map.TILE)
            sideRects[1].set(p6x, p6y, 1, 1);
        else
            sideRects[1].set(-1, -1, 0, 0);
    }
}
