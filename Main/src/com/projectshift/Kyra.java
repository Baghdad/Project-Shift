package com.projectshift;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Author: Bogdanov Kirill
 * Date: 09.11.12
 * Time: 1:40
 */
public class Kyra {
    static final int IDLE = 0;
    static final int RUN = 1;
    static final int SPAWN = 2;
    static final int RIGHT = 1;
    static final int LEFT = -1;
    static final float ACCELERATION = 20f;
    static final float GRAVITY = 20.0f;
    static final float MAX_VEL = 6f;
    static final float DAMP = 0.90f;

    Vector2 pos = new Vector2();
    Vector2 accel = new Vector2();
    Vector2 vel = new Vector2();
    public Rectangle bounds = new Rectangle();

    int state = SPAWN;
    float stateTime = 0;
    int dir = LEFT;
    Map map;
    boolean grounded = false;

    public Kyra(Map map, float x, float y) {
        this.map = map;
        pos.x = x;
        pos.y = y;
        bounds.width = 0.6f;
        bounds.height = 0.8f;
        bounds.x = pos.x + 0.2f;
        bounds.y = pos.y;
        state = SPAWN;
        stateTime = 0;
    }

    public void update(float deltaTime) {
        processKeys();
        accel.y = -GRAVITY;
        accel.mul(deltaTime);
        vel.add(accel.x, accel.y);
        if (accel.x == 0) vel.x *= DAMP;
        if (vel.x > MAX_VEL) vel.x = MAX_VEL;
        if (vel.x < -MAX_VEL) vel.x = -MAX_VEL;
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
        if (Gdx.input.isKeyPressed(Keys.D)) {
            state = RUN;
            dir = RIGHT;
            accel.x = ACCELERATION * dir;
        } else if (Gdx.input.isKeyPressed(Keys.A)) {
            state = RUN;
            dir = LEFT;
            accel.x = ACCELERATION * dir;
        } else {
            state = IDLE;
            accel.x = 0;
        }
    }

    Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

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

        bounds.y += vel.y;
        fetchCollidableRects();
        for (Rectangle rect : r) {
            if (bounds.overlaps(rect)) {
                if (vel.y < 0) {
                    bounds.y = rect.y + rect.height + 0.01f;
                    grounded = true;
                    if (state != SPAWN) state = Math.abs(accel.x) > 0.1f ? RUN : IDLE;
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

        int[][] tiles = map.tiles;
        int tile1 = tiles[p1x][map.tiles[0].length - 1 - p1y];
        int tile2 = tiles[p2x][map.tiles[0].length - 1 - p2y];
        int tile3 = tiles[p3x][map.tiles[0].length - 1 - p3y];
        int tile4 = tiles[p4x][map.tiles[0].length - 1 - p4y];

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

        r[4].set(-1, -1, 0, 0);
    }
}
