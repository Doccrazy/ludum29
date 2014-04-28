package de.doccrazy.ld29.game.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.actor.DiggerActor;
import de.doccrazy.ld29.game.level.TileType;
import de.doccrazy.ld29.game.world.GameState;
import de.doccrazy.ld29.game.world.GameWorld;

public class CheckpointLabel extends Label {
	private GameWorld world;

	private static final Map<TileType, String> NAMES = new HashMap<TileType, String>() {{
	    put(TileType.COAL, "Coal");
	    put(TileType.IRON, "Iron");
	    put(TileType.SILVER, "Silver");
	    put(TileType.GOLD, "Gold");
	    put(TileType.DIAMOND, "Diamond / final");
	}};

	public CheckpointLabel(GameWorld world) {
		super("", new LabelStyle(Resource.fontSmall, new Color(1f, 0.4f, 0.3f, 0.7f)));
		this.world = world;

		setPosition(20, 20);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
        setVisible(world.getGameState() == GameState.GAME || world.isGameFinished());
		TileType req = DiggerActor.requiredMineral(world.getDiggerLevel());
		setText("Level: " + (world.getDiggerLevel() + 1) + " (" + NAMES.get(req) + ")");
	}

}
