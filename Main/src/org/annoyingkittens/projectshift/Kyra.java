package org.annoyingkittens.projectshift;

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
    //направление движения
    static final int RIGHT = 1;
    static final int LEFT = -1;
    //скорости по осям
    static final float MAX_VEL = 3.5f;
    static final float JUMP_VELOCITY = 10;
    boolean jumpCheck = false;
    int sideCollideCheck = 0;
    //вектора
    Vector2 pos = new Vector2();//вектор позиции
    Vector2 vel = new Vector2();//вектор скорости
    Vector2 accel = new Vector2();//вектор ускорения
    Vector3 touchPoint = new Vector3();//вектор нажатия мыши
    //границы Киры
    public Rectangle bounds = new Rectangle();

    States state = States.SPAWN;
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
        state = States.SPAWN;
        stateTime = 0;
    }

    //метод, который вызывается через каждые deltaTime
    public void update(float deltaTime) {
        processKeys();
        //в шифте Кира не падает
        if (state != States.SHIFT) {
            accel.y = -10f;
        }
        //манипуляции при прыжке номер раз
        if (state == States.JUMP) {
            if (stateTime > 0.6 && !jumpCheck) {
                vel.y = JUMP_VELOCITY;
                jumpCheck = true;
            }
        }
        //изменения скорости/ускорения от времени
        accel.mul(deltaTime);
        vel.add(accel.x, accel.y);
        vel.mul(deltaTime);
        tryMove();
        vel.mul(1.0f / deltaTime);
        //проверка, что уже возродились (кандидат на выпил)
        if (state == States.SPAWN) {
            if (stateTime > 0.4f) {
                state = States.IDLE;
            }
        }
        //приращение времени состояния
        stateTime += deltaTime;
        //Gdx.app.debug("Shift", "velY " + vel.y);
        //Gdx.app.debug("Shift", "state " + state);
        Gdx.app.debug("Shift", "Jump " + jumpCheck);
    }

    //метод задает реакцию на нажатие кнопок
    public void processKeys() {
        if (state == States.SPAWN) return;
        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && state != States.JUMP) {
            stateTime = 0;
            state = States.SHIFT;
            vel.x = 0;
            vel.y = 0;
            if (Gdx.input.justTouched()) {
                touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                MapRenderer.cam.unproject(touchPoint);
                bounds.x = touchPoint.x;
                bounds.y = touchPoint.y;
            }
        } else if (Gdx.input.isKeyPressed(Keys.W) && state != States.JUMP && state != States.FALL) {
            stateTime = 0;
            state = States.JUMP;
            grounded = false;
        } else if (Gdx.input.isKeyPressed(Keys.D) && state != States.SHIFT && sideCollideCheck != 1) {
            if (state != States.JUMP && state != States.FALL) state = States.RUN;
            dir = RIGHT;
            vel.x = MAX_VEL * dir;
            sideCollideCheck = 0;
        } else if (Gdx.input.isKeyPressed(Keys.A) && state != States.SHIFT && sideCollideCheck != -1) {
            if (state != States.JUMP && state != States.FALL) state = States.RUN;
            dir = LEFT;
            vel.x = MAX_VEL * dir;
            sideCollideCheck = 0;
        } else {
            if (state != States.JUMP && state != States.FALL) state = States.IDLE;
            vel.x = 0;
        }
    }

    //коллидирующие квадраты
    Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
    Rectangle[] sideRects = {new Rectangle(), new Rectangle()};

    //пробуем двигаться и проверяем не столкнулись ли с чем
    private void tryMove() {
        bounds.x += vel.x;
        fetchCollidableRects();
        for (Rectangle rect : r) {
            if (bounds.overlaps(rect)) {
                if (vel.x < 0) {
                    if (state != States.JUMP && state != States.FALL)
                        sideCollideCheck = -1;
                    bounds.x = rect.x + rect.width + 0.01f;
                } else {
                    if (state != States.JUMP && state != States.FALL)
                        sideCollideCheck = 1;
                    bounds.x = rect.x - bounds.width - 0.01f;
                }
                vel.x = 0;
            }
        }


        for (Rectangle rect : sideRects) {
            if (bounds.overlaps(rect)) {
                if (vel.x < 0) {
                    bounds.x = rect.x + rect.width + 0.01f;
                } else {
                    bounds.x = rect.x - bounds.width - 0.01f;
                }
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
                    if (state != States.SPAWN && state != States.JUMP) {
                        state = Math.abs(vel.x) > 0 ? States.RUN : States.IDLE;
                    }
                    //манипуляции при прыжке номер два
                    if (stateTime > 0.6 && state == States.JUMP) {
                        state = States.IDLE;
                        jumpCheck = false;
                    }
                } else
                    bounds.y = rect.y - bounds.height - 0.01f;
                vel.y = 0;
            }
        }
        //Gdx.app.debug("Shift", "velY2 " + vel.y);
        if (vel.y < -0.01 && state != States.JUMP) {
            state = States.FALL;
        }

        pos.x = bounds.x - 0.2f;
        pos.y = bounds.y;
    }

    //расстановка коллидирующих квадратов
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
