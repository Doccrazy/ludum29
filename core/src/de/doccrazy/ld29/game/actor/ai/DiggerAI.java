package de.doccrazy.ld29.game.actor.ai;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import de.doccrazy.ld29.game.world.GameState;
import de.doccrazy.ld29.game.world.GameWorld;

public class DiggerAI extends Action {
    private GameWorld world;

    private SequenceAction goalSequence;

    public DiggerAI(GameWorld w) {
        this.world = w;

        goalSequence = new SequenceAction();
        if (world.getGameState() == GameState.SPAWN) {
            goalSequence.addAction(new InitialWalkAction());
        }
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);

        if (actor != null) {
            getActor().addAction(goalSequence);
        }
    }

    @Override
    public boolean act(float delta) {
        if (world.getGameState() == GameState.GAME && goalSequence.getActor() == null) {
            goalSequence.reset();
            goalSequence.addAction(new FindMineralStrategy(world));
            getActor().addAction(goalSequence);
        }
        if (world.isGameFinished()) {
            return true;
        }
        return false;
    }

    public void onLevelUp(int diggerLevel) {
        goalSequence.restart();
    }
}
