
package com.badlogic.cubocy;

import com.badlogic.cubocy.screens.GameScreen;
import com.badlogic.gdx.Game;

public class Cubocy extends Game {
	public void create () {
		setScreen(new GameScreen(this));
	}
}
