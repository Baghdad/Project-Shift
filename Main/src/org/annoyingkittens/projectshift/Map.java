package org.annoyingkittens.projectshift;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Author: Bogdanov Kirill
 * Date: 01.09.12
 * Time: 19:35
 */
public class Map {
    static int TILE = 0xffffff;
    static int START = 0xff0000;
    static int ENEMY = 0x00ff00;

    int tiles[][];
    public Kyra kyra;
    Vector3 touchPoint = new Vector3();
    Rectangle soundBounds;

    public Map() {
        loadBinary();
    }

    private void loadBinary() {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("data/map.png"));
        tiles = new int[pixmap.getWidth()][pixmap.getHeight()];
        for (int y = 0; y < 35; y++) {
            for (int x = 0; x < 150; x++) {
                int pix = (pixmap.getPixel(x, y) >>> 8) & 0xffffff;
                if (match(START, pix)) {
                    kyra = new Kyra(this, x, pixmap.getHeight() - 1 - y);
                    kyra.state = States.SPAWN;

                } else if (match(ENEMY, pix)) {

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
        if (Gdx.input.justTouched()) {
            MapRenderer.cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
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
    }

    boolean match(int src, int dst) {
        return src == dst;
    }
}
