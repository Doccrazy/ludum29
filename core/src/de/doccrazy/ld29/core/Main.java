package de.doccrazy.ld29.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;

import de.doccrazy.ld29.game.GameScreen;

public class Main extends Game {
    public static final String GAME_TITLE = "AntiMine 3000";

	private GameScreen gameScreen;
	private FPSLogger fps;

	@Override
	public void create () {
		Resource.init();

		gameScreen = new GameScreen();
		setScreen(gameScreen);

		fps = new FPSLogger();
	}

	@Override
	public void render () {
		super.render();
		//fps.log();
	}
}
