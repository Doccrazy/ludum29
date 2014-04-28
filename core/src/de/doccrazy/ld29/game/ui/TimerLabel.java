package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.world.GameState;
import de.doccrazy.ld29.game.world.GameWorld;

public class TimerLabel extends Label {
	private GameWorld world;

	public TimerLabel(GameWorld world) {
		super("", new LabelStyle(Resource.fontSmall, new Color(1f, 0.4f, 0.3f, 0.7f)));
		this.world = world;

		setAlignment(Align.right);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
        setVisible(world.getGameState() == GameState.GAME || world.isGameFinished());
		setPosition(getStage().getWidth() - 20, getStage().getHeight() - 20);

		setText("Time: " + (int)world.getGameTime());
	}

}
