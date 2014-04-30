package de.doccrazy.ld29.game.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;

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

    public final Stage stage; // stage containing game actors (not GUI, but actual game elements)
    public final World box2dWorld; // box2d world
    public final RayHandler rayHandler;

	private float deltaCache;
	private ActorListener actorListener;

	private LevelActor currentLevel;
	private List<DiggerActor> diggers = new ArrayList<>();
	private List<Integer> deathList = new ArrayList<>();
	int spawnCounter;
	float spawnDelay = 2f;
    private boolean respawn;
    private float levelTimer;

    private int diggerLevel;
    private boolean clearLavaUnlock = false;

    private int score;

    private GameState gameState;
    private float gameTime;

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
		startSpawn();
		startGame();
    }

    private void createWorld() {
        Level level = new Level(GameRules.LEVEL_WIDTH + 2, GameRules.LEVEL_HEIGHT + 1);
        level.random();
        currentLevel = new LevelActor(this, level, -1);
    }

    public void startSpawn() {
        gameState = GameState.SPAWN;
        spawnCounter = 2;
    }

    public void startGame() {
        gameState = GameState.GAME;
        gameTime = GameRules.GAME_TIME;
        diggerLevel = 0;
        levelTimer = 0;
        Resource.game.play();
    }

    public void stopGame(boolean victory) {
        spawnCounter = 0;
        gameState = victory ? GameState.VICTORY : GameState.DEFEAT;
        Resource.game.stop();
        Resource.outro.play();
    }

    public void spawnDigger(Vector2 pos, Mood initialMood) {
        DiggerActor digger = new DiggerActor(this, pos);
        digger.setMood(initialMood, 3f);
        digger.setLevel(diggerLevel);
        diggers.add(digger);
        digger.addAction(new DiggerAI(this));
        spawnCounter--;
        spawnDelay = GameRules.RESPAWN_DELAY;
    }

    public void diggerLevelUp() {
        if (diggerLevel == GameRules.MAX_LEVEL - 1) {
            Resource.levelUp.play();
            stopGame(false);
            return;
        }
        diggerLevel++;
        levelTimer = 0;
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

    public int getDiggerLevel() {
        return diggerLevel;
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
        	    Point pos = new Point(RandomUtils.nextInt(3, GameRules.LEVEL_WIDTH-2), 1);
        	    if (gameState == GameState.SPAWN) {
        	        pos.x = Math.random() > 0.5 ? -1 : GameRules.LEVEL_WIDTH + 1;
        	    } else {
        	    	int tries = 0;
        	    	while (currentLevel.getLevel().tileAt(pos.x, 0) == null && tries < 15) {
        	    		pos.x = RandomUtils.nextInt(3, GameRules.LEVEL_WIDTH-2);
        	    		tries++;
        	    	}
        	    }
        	    spawnDigger(currentLevel.tileToWorld(pos), mood);
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

        levelTimer += delta;
        if (levelTimer > GameRules.SUPPORT_DELAY) {
            respawn = false;
            spawnCounter++;
            levelTimer = 0;
        }
        if (gameState == GameState.GAME) {
            gameTime -= delta;
            if (gameTime < 0) {
                stopGame(true);
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

    public void diggerDie(DiggerActor digger) {
        Resource.die.play();
        deathList.add(digger.getLevel());
        score += digger.getLevel() * 10;
    }

    public List<Integer> getDeathList() {
        return deathList;
    }

    public int getScore() {
        return score;
    }

    public float getGameTime() {
        return gameTime;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isGameFinished() {
        return gameState == GameState.VICTORY || gameState == GameState.DEFEAT;
    }
}
