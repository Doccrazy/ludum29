package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import de.doccrazy.ld29.core.Debug;
import de.doccrazy.ld29.game.GameRenderer;
import de.doccrazy.ld29.game.world.GameWorld;

public class UiInputListener extends InputListener {
	private GameWorld world;
	private GameRenderer renderer;

	public UiInputListener(GameWorld world, GameRenderer renderer) {
        this.world = world;
		this.renderer = renderer;
	}

	@Override
    public boolean keyDown(InputEvent event, int keycode) {
		if (keycode == Keys.ENTER/* && world.isGameFinished()*/) {
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
}
