package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.world.GameWorld;

public class HighscoreLabel extends Label {
	private GameWorld world;
	private int highscore = 0;

	public HighscoreLabel(GameWorld world) {
		super("", new LabelStyle(Resource.fontSmall, new Color(1f, 0.4f, 0.3f, 0.7f)));
		this.world = world;

		setAlignment(Align.center);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		setPosition(0, getStage().getHeight() * 0.55f);
		setWidth(getStage().getWidth());

		highscore = Math.max(highscore, world.getScore());
        setVisible(world.isGameFinished());
		setText("Your Highscore: " + highscore);
	}

}
