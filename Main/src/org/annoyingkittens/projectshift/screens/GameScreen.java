package org.annoyingkittens.projectshift.screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.annoyingkittens.projectshift.Map;
import org.annoyingkittens.projectshift.MapRenderer;

/**
 * Author: Bogdanov Kirill
 * Date: 01.09.12
 * Time: 19:31
 */
public class GameScreen extends ShiftScreen {
    Map map;
    MapRenderer renderer;

    public GameScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        map = new Map();
        renderer = new MapRenderer(map);
    }

    public void render(float delta) {
        delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
        map.update(delta);
        if (map.getKyra().healthPoints <= 0) {
            renderer.dispose();
            map = new Map();
            renderer = new MapRenderer(map);
        }
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render(delta);
    }

    public void hide() {
        Gdx.app.debug("Shift", "dispose game screen");
        renderer.dispose();
    }
}
