package org.annoyingkittens.projectshift.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * Author: Bogdanov Kirill
 * Date: 01.09.12
 * Time: 19:17
 */
abstract class ShiftScreen implements Screen {
    Game game;

    public ShiftScreen(Game game) {
        this.game = game;
    }

    public void resize(int width, int height) {
    }

    public void show() {
    }

    public void hide() {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void dispose() {
    }
}
