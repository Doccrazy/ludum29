package de.doccrazy.ld29.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.doccrazy.ld29.core.Main;
import de.doccrazy.ld29.game.GameScreen;

public class DesktopLauncher {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = GameScreen.SCREEN_WIDTH;
		config.height = GameScreen.SCREEN_HEIGHT;
		config.vSyncEnabled = true;
		config.title = Main.GAME_TITLE + " (a Ludum Dare 29 game by Doccrazy)";
		new LwjglApplication(new Main(), config);
	}
}
