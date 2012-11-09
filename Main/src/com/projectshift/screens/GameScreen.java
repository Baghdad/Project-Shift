package com.projectshift.screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.projectshift.Map;
import com.projectshift.MapRenderer;
import com.projectshift.OnscreenControlRenderer;

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
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        renderer.render(delta);
    }

    public void hide() {
        Gdx.app.debug("Cubocy", "dispose game screen");
        renderer.dispose();
    }
}
