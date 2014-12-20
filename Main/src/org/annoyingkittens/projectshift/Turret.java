package org.annoyingkittens.projectshift;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Author: Bogdanov Kirill
 * Date: 20.12.2014
 * Time: 0:01
 */
public class Turret {
    Vector2 pointer = new Vector2();
    Rectangle bounds = new Rectangle();
    Vector2 pos = new Vector2();
    int[][] tiles;
    TurretStates state = TurretStates.STANDBY;

    public Turret (Map map, float x, float y) {
        tiles = map.getTiles();
        bounds.x = x;
        bounds.y = y;
        bounds.height = 1f;
        bounds.width = 1f;
        pos.x = x;
        pos.y = y;
    }

    public void update (float deltaTime) {

    }
}
