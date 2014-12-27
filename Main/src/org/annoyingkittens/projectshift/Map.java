package org.annoyingkittens.projectshift;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Author: Bogdanov Kirill
 * Date: 01.09.12
 * Time: 19:35
 */
public class Map {
    public static int TILE = 0xffffff;
    static int LEDGEL = 0x0000ff;
    static int LEDGER = 0x000100;
    static int START = 0xff0000;
    static int ENEMY = 0x00ff00;

    private int tiles[][];
    private Kyra kyra;
    private Array<Turret> turrets = new Array<Turret>();
    Vector3 touchPoint = new Vector3();
    Rectangle soundBounds;
    SecurityStates state = SecurityStates.UNDEDECTED;

    public Map() {
        loadBinary();
    }

    private void loadBinary() {
        Gdx.app.debug("Shift", 0x000100 + "");
        Pixmap pixmap = new Pixmap(Gdx.files.internal("data/map.png"));
        tiles = new int[pixmap.getWidth()][pixmap.getHeight()];
        for (int y = 0; y < 35; y++) {
            for (int x = 0; x < 150; x++) {
                int pix = (pixmap.getPixel(x, y) >>> 8) & 0xffffff;
                if (match(START, pix)) {
                    kyra = new Kyra(this, x, pixmap.getHeight() - 1 - y);
                    kyra.state = States.SPAWN;
                } else if (match(ENEMY, pix)) {
                    turrets.add(new Turret(this, x, pixmap.getHeight() - 1 - y));
                } else if (match(LEDGEL, pix) && match(TILE, tiles[x - 1][y])) {
                    tiles[x][y] = pix + 1;
                } else {
                    tiles[x][y] = pix;
                }
            }
        }
        /*for (int[] tile: tiles) {
            System.out.println(Arrays.toString(tile));
        }*/
    }

    public void update(float deltaTime) {
        Gdx.app.debug("Shift", "" + state);
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            MapRenderer.cam.unproject(touchPoint);
            soundBounds = new Rectangle(MapRenderer.cam.position.x + 6, MapRenderer.cam.position.y + 4, 1, 1);
            if (OverlapTester.pointInRectangle(soundBounds, touchPoint.x, touchPoint.y)) {
                Assets.soundEnabled = !Assets.soundEnabled;
                if (Assets.soundEnabled)
                    Assets.shiftMusic.play();
                else
                    Assets.shiftMusic.pause();
            }
        }
        kyra.update(deltaTime);
        if (kyra.state != States.SHIFT) {
            boolean danger = true;
            for (Turret turret : turrets) {
                turret.update(deltaTime);
                danger &= turret.isDanger();
            }
            if (!danger) {
                state = SecurityStates.UNDEDECTED;
            }
        }
    }

    public static boolean match(int src, int dst) {
        return src == dst;
    }

    public int[][] getTiles() {
        return tiles;
    }

    public Kyra getKyra() {
        return kyra;
    }

    public Array<Turret> getTurret() {
        return turrets;
    }
}
