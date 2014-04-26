package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import de.doccrazy.ld29.core.Debug;
import de.doccrazy.ld29.game.GameRenderer;
import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.level.Level;

public class UiInputListener extends InputListener {
	private GameWorld world;
	private GameRenderer renderer;
    private Stage uiStage;

	public UiInputListener(Stage uiStage, GameWorld world, GameRenderer renderer) {
		this.uiStage = uiStage;
        this.world = world;
		this.renderer = renderer;
	}

	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		if (keycode == Keys.ENTER) {
			world.reset();
		}
		if (Debug.ON) {
			if (keycode == Keys.Z) {
				renderer.setZoomDelta(1f);
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(InputEvent event, int keycode) {
        if (Debug.ON) {
            if (keycode == Keys.Z) {
                renderer.setZoomDelta(-2f);
            }
        }
        return false;
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
	    Vector2 screen = uiStage.stageToScreenCoordinates(new Vector2(x, y));
	    Vector2 coord = renderer.toWorldCoordinates(screen);
	    if (button == 0) {
	        world.getCurrentLevel().clearTile(Level.getTileIndex(coord));
	    }
	    if (button == 1) {
	        world.spawnDigger(coord);
	    }
	    return false;
	}
}
