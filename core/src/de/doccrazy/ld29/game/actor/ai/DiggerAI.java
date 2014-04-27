package de.doccrazy.ld29.game.actor.ai;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.actor.DiggerActor;

public class DiggerAI extends Action {
    private GameWorld world;

    private SequenceAction goalSequence;

    public DiggerAI(GameWorld w) {
        this.world = w;

        goalSequence = new SequenceAction();
        if (Math.random() < 0.5) {
            goalSequence.addAction(new SidewaysGoal(world));
        } else {
            goalSequence.addAction(new DownGoal(world));
        }
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);

        getActor().addAction(goalSequence);
    }

    @Override
    public boolean act(float delta) {
        return false;
    }

    private DiggerActor getDigger() {
        return (DiggerActor) actor;
    }
}
