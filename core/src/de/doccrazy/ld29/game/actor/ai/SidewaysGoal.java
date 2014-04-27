package de.doccrazy.ld29.game.actor.ai;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import de.doccrazy.ld29.game.GameWorld;

public class SidewaysGoal extends SequenceAction {
    private GameWorld world;

    public SidewaysGoal(GameWorld world) {
        this.world = world;
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);
        addAction(new CenterAction());
        addAction(new DigForwardAction(world));
        addAction(new MoveForwardAction(true));
        addAction(new DigDownAction(world));
        addAction(new DelayAction(0.3f));
    }

    @Override
    public boolean act(float delta) {
        if (super.act(delta)) {
            restart();
        }
        return false;
    }

}
