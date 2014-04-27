package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.doccrazy.ld29.core.Debug;
import de.doccrazy.ld29.game.GameRenderer;
import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.actor.FloatingTextActor;

public class UiRoot extends Table {
	private GameWorld world;
    private GameRenderer renderer;
    private GameInputListener input;
    private Toolbar toolbar;

	public UiRoot(Stage stage, GameWorld world, GameRenderer renderer) {
		this.world = world;
        this.renderer = renderer;
        stage.addActor(this);
		setFillParent(true);
		if (Debug.ON) {
		    debug();
		}

        input = new GameInputListener(this);
        world.stage.addListener(input);

		top();
		toolbar = new Toolbar(this);
		toolbar.setVisible(false);
		add(toolbar).expandX().left();
		stage.addActor(new IntroLabel(this));
	}

	@Override
	public void act(float delta) {
	    super.act(delta);
	    for (Actor a : world.stage.getActors()) {
	        if (a instanceof FloatingTextActor && !((FloatingTextActor) a).isRendered()) {
	            ((FloatingTextActor)a).setRendered(true);
	            new FloatingTextLabel(getStage(), (FloatingTextActor)a);
	        }
	    }
	}

	public void showToolbar() {
	    toolbar.setVisible(true);
	}

	public GameWorld getWorld() {
        return world;
    }

	public GameRenderer getRenderer() {
        return renderer;
    }

	public GameInputListener getInput() {
        return input;
    }
}
