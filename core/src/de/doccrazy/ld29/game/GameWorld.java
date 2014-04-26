package de.doccrazy.ld29.game;

import java.util.Arrays;
import java.util.List;

import box2dLight.RayHandler;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import de.doccrazy.ld29.game.actor.LevelActor;
import de.doccrazy.ld29.game.base.ActorContactListener;
import de.doccrazy.ld29.game.base.ActorListener;
import de.doccrazy.ld29.game.base.Box2dActor;
import de.doccrazy.ld29.game.level.Level;

public class GameWorld {
    private static float PHYSICS_STEP = 1f/300f;

    public static final Vector2 GRAVITY = new Vector2(0, -9.8f);
    private static final Vector2 SPAWN = new Vector2(-4f, 4.5f);

    public final Stage stage; // stage containing game actors (not GUI, but actual game elements)
    public final World box2dWorld; // box2d world
    public final RayHandler rayHandler;

	private float deltaCache;
	private ActorListener actorListener;

	private LevelActor currentLevel;

    public GameWorld() {
        box2dWorld = new World(GRAVITY, true);
        box2dWorld.setContactListener(new ActorContactListener());
        stage = new Stage(); // create the game stage
        rayHandler = new RayHandler(box2dWorld);

        createWorld();
    }

    public void reset() {
		currentLevel = null;
		stage.setKeyboardFocus(null);

		List<Actor> actors = Arrays.asList(stage.getActors().toArray());
		for (Actor actor : actors) {
    		actor.remove();
		}

		createWorld();
    }

    private void createWorld() {
        Level level = new Level(40, 24);
        level.random();
        currentLevel = new LevelActor(this, level);
        currentLevel.setOrigin(0, 0);
    }

    public void update(float delta) {
    	deltaCache += delta;

    	while (deltaCache >= PHYSICS_STEP) {
    		// perform game logic here
    		stage.act(PHYSICS_STEP); // update game stage
    		box2dWorld.step(PHYSICS_STEP, 6, 3); // update box2d world
    		deltaCache -= PHYSICS_STEP;
    	}
    }

    public LevelActor getCurrentLevel() {
        return currentLevel;
    }

	public void setActorListener(ActorListener actorListener) {
		this.actorListener = actorListener;
	}

	public void onActorAdded(Box2dActor actor) {
		if (actorListener != null) {
			actorListener.actorAdded(actor);
		}
	}

	public void onActorRemoved(Box2dActor actor) {
		if (actorListener != null) {
			actorListener.actorRemoved(actor);
		}
	}
}
