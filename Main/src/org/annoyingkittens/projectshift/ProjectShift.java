package org.annoyingkittens.projectshift;

import com.badlogic.gdx.Game;
import org.annoyingkittens.projectshift.screens.GameScreen;

/**
 * Author: Bogdanov Kirill
 * Date: 01.09.12
 * Time: 18:41
 */
public class ProjectShift extends Game {
    public void create() {
        Assets.load();
        setScreen(new GameScreen(this));
    }

    public void dispose() {
        super.dispose();
        getScreen().dispose();
    }
}
