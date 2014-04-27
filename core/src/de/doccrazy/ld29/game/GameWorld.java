package de.doccrazy.ld29.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import box2dLight.RayHandler;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.actor.DiggerActor;
import de.doccrazy.ld29.game.actor.LevelActor;
import de.doccrazy.ld29.game.actor.Mood;
import de.doccrazy.ld29.game.actor.ai.DiggerAI;
import de.doccrazy.ld29.game.base.ActorContactListener;
import de.doccrazy.ld29.game.base.ActorListener;
import de.doccrazy.ld29.game.base.Box2dActor;
import de.doccrazy.ld29.game.level.Level;

public class GameWorld {
    private static float PHYSICS_STEP = 1f/300f;

    public static final Vector2 GRAVITY = new Vector2(0, -9.8f);
    private static final Vector2 SPAWN = new Vector2(-4f, 4.5f);

    public static final int LEVEL_WIDTH = 46;

    public final Stage stage; // stage containing game actors (not GUI, but actual game elements)
    public final World box2dWorld; // box2d world
    public final RayHandler rayHandler;

	private float deltaCache;
	private ActorListener actorListener;

	private LevelActor currentLevel;
	private List<DiggerActor> diggers = new ArrayList<>();
	int spawnCounter;
	float spawnDelay = 2f;
    private boolean respawn;

    private int diggerLevel;
    private boolean clearLavaUnlock = false;

    private boolean gameStarted;

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
        Level level = new Level(LEVEL_WIDTH + 2, 24);
        level.random();
        currentLevel = new LevelActor(this, level, -1);
    }

    public void startSpawn() {
        spawnCounter = 2;
    }

    public void startGame() {
        gameStarted = true;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void spawnDigger(Vector2 pos, Mood initialMood) {
        DiggerActor digger = new DiggerActor(this, pos);
        digger.setMood(initialMood, 3f);
        digger.setLevel(diggerLevel);
        diggers.add(digger);
        digger.addAction(new DiggerAI(this));
        spawnCounter--;
        spawnDelay = 2f;
    }

    public void diggerLevelUp() {
        diggerLevel++;
        for (DiggerActor digger : diggers) {
            digger.setLevel(diggerLevel);
            for (Action a : digger.getActions()) {
                if (a instanceof DiggerAI) {
                    ((DiggerAI)a).onLevelUp(diggerLevel);
                }
            }
        }
        Resource.levelUp.play();
    }

    public boolean isClearLavaUnlock() {
        return clearLavaUnlock;
    }

    public void setClearLavaUnlock(boolean clearLavaUnlock) {
        this.clearLavaUnlock = clearLavaUnlock;
    }

    public void update(float delta) {
    	deltaCache += delta;

    	while (deltaCache >= PHYSICS_STEP) {
    		// perform game logic here
    		stage.act(PHYSICS_STEP); // update game stage
    		box2dWorld.step(PHYSICS_STEP, 6, 3); // update box2d world
    		deltaCache -= PHYSICS_STEP;
    	}

    	if (spawnCounter > 0) {
    	    spawnDelay -= delta;
    	    if (spawnDelay < 0) {
        	    Mood mood = Math.random() > 0.5 ? Mood.DIAMOND : Mood.MONEY;
        	    if (respawn) {
        	        mood = Math.random() > 0.5 ? Mood.ANGRY : Mood.PISSED;
        	        Resource.respawn.play();
        	    }
        	    Vector2 pos = new Vector2((float)Math.random() * 32 + 4, 1);
        	    if (!gameStarted) {
        	        pos.x = Math.random() > 0.5 ? -0.5f : LEVEL_WIDTH + 0.5f;
        	    }
        	    spawnDigger(pos, mood);
    	    }
    	}
    	for (Iterator<DiggerActor> it = diggers.iterator(); it.hasNext(); ) {
    	    DiggerActor actor = it.next();
    	    if (actor.isDead()) {
    	        it.remove();
    	        respawn = true;
    	        spawnCounter++;
    	    }
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
