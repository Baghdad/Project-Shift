package com.projectshift;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

/**
 * Author: Bogdanov Kirill
 * Date: 01.09.12
 * Time: 19:35
 */
public class Map {
    static int Empty = 0;
    static int TILE = 0xffffff;
    static int START = 0xff0000;
    static int ENEMY = 0x00ff00;

    int tiles[][];
    public Kyra kyra;

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
                    kyra.state = Kyra.SPAWN;
                } else if(match(ENEMY, pix)) {

                } else {
                    tiles[x][y] = pix;
                }
            }
        }
        //for (int[] tile: tiles) {
            //System.out.println(Arrays.toString(tile));
        //}
    }

    public void update (float deltaTime) {
    		kyra.update(deltaTime);
    	}

    boolean match(int src, int dst) {
    		return src == dst;
    	}
}
