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
    Vector2 startPoint = new Vector2();
    Rectangle bounds = new Rectangle();
    Vector2 pos = new Vector2();
    Map map;
    TurretStates state = TurretStates.STANDBY;
    float angle = 90;
    boolean incFlag = true;

    public Turret(Map map, float x, float y) {
        this.map = map;
        bounds.x = x;
        bounds.y = y;
        bounds.height = 1f;
        bounds.width = 1f;
        pos.x = x;
        pos.y = y;
        startPoint.x = x;
        startPoint.y = y + 0.5f;
    }

    public void update(float deltaTime) {
        //Gdx.app.debug("Shift", "" + state);
        int ix = (int) startPoint.x;
        int iy = map.getTiles()[0].length - 1 - (int) startPoint.y;
        float deltaX = 0.25f * (float) Math.cos(angle * Math.PI / 180);
        float deltaY = 0.25f * (float) Math.sin(angle * Math.PI / 180);
        for (float x = ix, y = iy; x > 0 && y > 0 && x < map.getTiles()[0].length && y < map.getTiles().length;
             x = x + deltaX, y = y - deltaY) {
            if (map.getTiles()[(int) Math.ceil(x)][(int) Math.ceil(y)] == Map.TILE) {
                if (state == TurretStates.FIRE) {
                    state = TurretStates.STANDBY;
                }
                pointer.set(x, map.getTiles()[0].length - y - 1);
                break;
            }
            if (OverlapTester.pointInRectangle(map.getKyra().bounds, x, map.getTiles()[0].length - y - 1)) {
                Rectangle kyra = map.getKyra().bounds;
                pointer.set(kyra.x + kyra.width / 2, kyra.y + kyra.height / 2);
                float dx = pointer.x - startPoint.x;
                float dy = pointer.y - startPoint.y;
                float len = (float) Math.sqrt(dx * dx + dy * dy);
                angle = (float) (Math.acos(dx / len) * 180 / Math.PI);
                state = TurretStates.FIRE;
                map.state = SecurityStates.DANGER;
                //Gdx.app.debug("Shift", angle + " - " + pointer.angle());
                break;
            }
        }
        if (state == TurretStates.FIRE) {
            map.getKyra().healthPoints--;
        }
        if (state == TurretStates.STANDBY) {
            //Gdx.app.debug("Shift", "" + angle);
            if (angle >= 180) {
                incFlag = false;
            }
            if (angle <= 0) {
                incFlag = true;
            }
            angle = incFlag ? ++angle : --angle;
        }
    }

    public boolean isDanger() {
        return state == TurretStates.FIRE;
    }
}
