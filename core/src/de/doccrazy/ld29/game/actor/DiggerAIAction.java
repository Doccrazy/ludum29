package de.doccrazy.ld29.game.actor;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import de.doccrazy.ld29.game.GameWorld;

public class DiggerAIAction extends Action {
    private GameWorld world;

    private SequenceAction sequence;

    public DiggerAIAction(GameWorld w) {
        this.world = w;

        sequence = new SequenceAction() {
            @Override
            public boolean act(float delta) {
                if (super.act(delta)) {
                    sequenceDone();
                }
                return false;
            }
        };
        sequence.addAction(new CenterAction());
        sequence.addAction(new DigForwardAction(world));
        sequence.addAction(new MoveForwardAction(true));
        sequence.addAction(new DigDownAction(world));
        sequence.addAction(new DelayAction(0.3f));
    }

    protected void sequenceDone() {
        sequence.restart();
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);

        getActor().addAction(sequence);
    }

    @Override
    public boolean act(float delta) {
        return false;
    }

    private DiggerActor getDigger() {
        return (DiggerActor) actor;
    }
}
