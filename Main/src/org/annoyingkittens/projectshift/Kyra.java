package org.annoyingkittens.projectshift;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Arrays;

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
    static float MAX_VEL = 3.5f;
    static final float JUMP_VELOCITY = 5;
    private boolean jumpCheck = false;
    private double droopTime = 0;
    private int sideCollideCheck = 0;
    //вектора
    private Vector2 pos = new Vector2();//вектор позиции
    private Vector2 vel = new Vector2();//вектор скорости
    private Vector2 accel = new Vector2();//вектор ускорения
    private Vector3 touchPoint = new Vector3();//вектор нажатия мыши
    //границы Киры
    Rectangle bounds = new Rectangle();
    int lastX = 0;
    int shiftPoints = 300;
    int shiftDelay = 30;
    int healthPoints = 100;


    States state = States.SPAWN;
    float stateTime = 0;
    int dir = RIGHT;
    int[][] tiles;
    boolean grounded = false;

    public Kyra(Map map, float x, float y) {
        tiles = map.getTiles();
        pos.x = x;
        pos.y = y;
        bounds.width = 1f;
        bounds.height = 2f;
        bounds.x = pos.x + 0.5f;
        bounds.y = pos.y;
        state = States.SPAWN;
        stateTime = 0;
    }

    //метод, который вызывается через каждые deltaTime
    public void update(float deltaTime) {
        Gdx.app.debug("Shift", "state " + state);
        processKeys();
        //в шифте Кира не падает
        if (state != States.SHIFT && state != States.HANG && state != States.CLIMB) {
            accel.y = -10f;
        }

        if (state == States.RUN && Gdx.input.isKeyPressed(Keys.S)) {
            state = States.CROUCH_MOVE;
        }

        if (state == States.CROUCH_MOVE) {
            MAX_VEL = 2f;
        } else {
            MAX_VEL = 3.5f;
        }

        if (state == States.CROUCH_IDLE || state == States.CROUCH_MOVE) {
            bounds.height = 1f;
        } else {
            bounds.height = 2f;
        }

        if (state == States.SHIFT) {
            shiftPoints -= 3;
        } else if (shiftPoints < 300) {
            if (shiftPoints == 0) {
                shiftDelay = 0;
            }
            shiftDelay++;
            shiftPoints++;
        }

        //манипуляции при прыжке номер раз
        if (state == States.JUMP) {
            if (!jumpCheck) {
                vel.y = JUMP_VELOCITY;
                jumpCheck = true;
            }
        }
        if (state == States.FALL) {
            sideCollideCheck = 0;
        }
        if (state == States.CLIMB) {
            if (stateTime < 2.01) {
                vel.x = 0;
                vel.y = 1;
            }
            if (stateTime >= 2.01 && stateTime < 3) {
                vel.y = 0;
                vel.x = dir == Kyra.RIGHT ? 1 : -1;
            }
            if (stateTime >= 3) {
                vel.x = 0;
                state = States.IDLE;
            }
        }
        //изменения скорости/ускорения от времени
        accel.scl(deltaTime);
        vel.add(accel.x, accel.y);
        vel.scl(deltaTime);
        tryMove();
        vel.scl(1.0f / deltaTime);
        //проверка, что уже возродились (кандидат на выпил)
        if (state == States.SPAWN) {
            if (stateTime > 0.4f) {
                state = States.IDLE;
            }
        }
        //приращение времени состояния
        if (state == States.SPAWN || state == States.JUMP || state == States.CLIMB) {
            stateTime += deltaTime;
            Gdx.app.debug("Shift", "" + stateTime);
        }
        droopTime += deltaTime;
    }

    //метод задает реакцию на нажатие кнопок
    public void processKeys() {
        if (state == States.SPAWN) return;
        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && state != States.JUMP && state != States.CLIMB &&
                shiftPoints > 0 && shiftDelay >= 30) {
            stateTime = 0;
            state = States.SHIFT;
            vel.x = 0;
            vel.y = 0;
            sideCollideCheck = 0;
            if (Gdx.input.justTouched()) {
                touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                MapRenderer.cam.unproject(touchPoint);
                if (tiles[(int) touchPoint.x][tiles[0].length - 1 - (int) touchPoint.y] != Map.TILE) {
                    bounds.x = touchPoint.x;
                    bounds.y = touchPoint.y;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Keys.W) && state != States.JUMP && state != States.FALL &&
                state != States.CLIMB) {
            stateTime = 0;
            if (state == States.HANG) {
                state = States.CLIMB;
                jumpCheck = false;
            } else {
                state = States.JUMP;
                grounded = false;
            }
        } else if (Gdx.input.isKeyPressed(Keys.D) && state != States.SHIFT && state != States.HANG &&
                state != States.CLIMB && sideCollideCheck != 1) {
            dir = RIGHT;
            if (state == States.IDLE) {
                state = States.RUN;
            }
            if (state == States.CROUCH_IDLE) {
                state = States.CROUCH_MOVE;
            }
            if ((state == States.CROUCH_MOVE || state == States.CROUCH_IDLE) && !Gdx.input.isKeyPressed(Keys.S)) {
                int px = (int) bounds.x;
                int py = (int) (bounds.y + bounds.height);
                int tile = tiles[px][tiles[0].length - 1 - py];
                if (tile != Map.TILE && lastX != 0) {
                    bounds.x = lastX + 1;
                    state = States.RUN;
                    lastX = 0;
                } else
                    lastX = px;
            }
            vel.x = MAX_VEL * dir;
            sideCollideCheck = 0;
        } else if (Gdx.input.isKeyPressed(Keys.A) && state != States.SHIFT && state != States.HANG &&
                state != States.CLIMB && sideCollideCheck != -1) {
            dir = LEFT;
            if (state == States.IDLE) {
                state = States.RUN;
            }
            if (state == States.CROUCH_IDLE) {
                state = States.CROUCH_MOVE;
            }
            if ((state == States.CROUCH_MOVE || state == States.CROUCH_IDLE) && !Gdx.input.isKeyPressed(Keys.S)) {
                int px = (int) bounds.x;
                int py = (int) (bounds.y + bounds.height);
                int tile = tiles[px][tiles[0].length - 1 - py];
                if (tile != Map.TILE && lastX != 0) {
                    bounds.x = lastX - 1;
                    state = States.RUN;
                    lastX = 0;
                } else
                    lastX = px;
            }
            vel.x = MAX_VEL * dir;
            sideCollideCheck = 0;
        } else if (Gdx.input.isKeyPressed(Keys.S)) {
            if (state == States.HANG) {
                droopTime = 0;
                state = States.FALL;
                jumpCheck = false;
            }
            if (state == States.IDLE) {
                state = States.CROUCH_IDLE;
                sideCollideCheck = 0;
            }
            if (state == States.RUN) {
                state = States.CROUCH_MOVE;
            }
            if (state == States.CROUCH_MOVE && !Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {
                state = States.CROUCH_IDLE;
                vel.x = 0;
            }
        } else {
            if (state != States.JUMP && state != States.FALL && state != States.HANG && state != States.CLIMB)
                if (state == States.CROUCH_IDLE || state == States.CROUCH_MOVE) {
                    int px = (int) bounds.x;
                    int py = (int) (bounds.y + bounds.height);
                    int tile = tiles[px][tiles[0].length - 1 - py];
                    if (tile == Map.TILE) {
                        state = States.CROUCH_IDLE;
                        lastX = px;
                    }
                } else {
                    state = States.IDLE;
                }
            vel.x = 0;
        }
    }

    //коллидирующие квадраты
    Rectangle[] allRects = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
    //для карабканья
    Rectangle[] ledgeRects = {new Rectangle(), new Rectangle()};

    //пробуем двигаться и проверяем не столкнулись ли с чем
    private void tryMove() {
        xMove();
        yMove();
    }

    private void xMove() {
        bounds.x += vel.x;
        fetchCollidableRects();
        for (Rectangle rect : allRects) {
            if (bounds.overlaps(rect) && state != States.CLIMB) {
                if (vel.x < 0) {
                    if (state != States.JUMP && state != States.FALL)
                        sideCollideCheck = -1;
                    bounds.x = rect.x + rect.width + 0.01f;
                } else {
                    if (state != States.JUMP && state != States.FALL)
                        sideCollideCheck = 1;
                    bounds.x = rect.x - rect.width - 0.01f;
                }
                vel.x = 0;
            }
        }

        if (bounds.overlaps(ledgeRects[0]) && droopTime > 0.5f && state != States.CLIMB && dir == RIGHT) {
            bounds.x = ledgeRects[0].x - 0.01f;
            bounds.y = ledgeRects[0].y - 0.01f;
            state = States.HANG;
            vel.x = 0;
            vel.y = 0;
        }

        if (bounds.overlaps(ledgeRects[1]) && droopTime > 0.5f && state != States.CLIMB && dir == LEFT) {
            bounds.x = ledgeRects[1].x + 0.01f;
            bounds.y = ledgeRects[1].y - 0.01f;
            state = States.HANG;
            vel.x = 0;
            vel.y = 0;
        }
    }

    private void yMove() {
        bounds.y += vel.y;
        fetchCollidableRects();
        for (Rectangle rect : Arrays.copyOf(allRects, 4)) {
            if (bounds.overlaps(rect) && state != States.CLIMB) {
                if (vel.y < 0) {
                    bounds.y = rect.y + rect.height + 0.01f;
                    grounded = true;
                    if (state != States.SPAWN && state != States.JUMP && state != States.HANG &&
                            state != States.CROUCH_IDLE && state != States.CROUCH_MOVE) {
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

        if (bounds.overlaps(ledgeRects[0]) && droopTime > 0.5f && state != States.CLIMB && dir == RIGHT) {
            bounds.y = ledgeRects[0].y - 0.01f;
            state = States.HANG;
            vel.y = 0;
        }

        if (bounds.overlaps(ledgeRects[1]) && droopTime > 0.5f && state != States.CLIMB && dir == LEFT) {
            bounds.y = ledgeRects[1].y - 0.01f;
            state = States.HANG;
            vel.y = 0;
        }

        if (vel.y < -0.01 && state != States.JUMP) {
            state = States.FALL;
        }

        pos.x = bounds.x;
        pos.y = bounds.y;
    }

    //расстановка коллидирующих квадратов
    private void fetchCollidableRects() {
        int p1x = (int) bounds.x;
        int p1y = (int) Math.floor(bounds.y);
        int p2x = (int) (bounds.x + bounds.width);
        int p2y = (int) Math.floor(bounds.y);

        int tile1 = tiles[p1x][tiles[0].length - 1 - p1y];
        int tile2 = tiles[p2x][tiles[0].length - 1 - p2y];

        if (tile1 == Map.TILE || tile1 == Map.LEDGEL || tile1 == Map.LEDGER)
            allRects[0].set(p1x, p1y, 1, 1);
        else
            allRects[0].set(-1, -1, 0, 0);
        if (tile2 == Map.TILE || tile2 == Map.LEDGEL || tile2 == Map.LEDGER)
            allRects[1].set(p2x, p2y, 1, 1);
        else
            allRects[1].set(-1, -1, 0, 0);

        if (state != States.CROUCH_IDLE && state != States.CROUCH_MOVE) {
            int p3x = (int) (bounds.x + bounds.width);
            int p3y = (int) (bounds.y + bounds.height);
            int p4x = (int) bounds.x;
            int p4y = (int) (bounds.y + bounds.height);
            int p5x = (int) bounds.x;
            int p5y = (int) (bounds.y + bounds.height / 2);
            int p6x = (int) (bounds.x + bounds.width);
            int p6y = (int) (bounds.y + bounds.height / 2);

            int tile3 = tiles[p3x][tiles[0].length - 1 - p3y];
            int tile4 = tiles[p4x][tiles[0].length - 1 - p4y];
            int tile5 = tiles[p5x][tiles[0].length - 1 - p5y];
            int tile6 = tiles[p6x][tiles[0].length - 1 - p6y];

            if (tile3 == Map.TILE)
                allRects[2].set(p3x, p3y, 1, 1);
            else
                allRects[2].set(-1, -1, 0, 0);
            if (tile4 == Map.TILE)
                allRects[3].set(p4x, p4y, 1, 1);
            else
                allRects[3].set(-1, -1, 0, 0);
            if (tile5 == Map.TILE)
                allRects[4].set(p5x, p5y, 1, 1);
            else
                allRects[4].set(-1, -1, 0, 0);
            if (tile6 == Map.TILE)
                allRects[5].set(p6x, p6y, 1, 1);
            else
                allRects[5].set(-1, -1, 0, 0);
            if (tile3 == Map.LEDGEL)
                ledgeRects[0].set(p5x, p5y, 1, 1);
            else
                ledgeRects[0].set(-1, -1, 0, 0);
            if (tile4 == Map.LEDGER)
                ledgeRects[1].set(p6x, p6y, 1, 1);
            else
                ledgeRects[1].set(-1, -1, 0, 0);
        }
    }

    public Vector2 getPosition() {
        return pos;
    }
}
