package de.doccrazy.ld29.game.actor.ai;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import de.doccrazy.ld29.game.world.GameWorld;

public class SidewaysGoal extends SequenceAction {
    private GameWorld world;
    private boolean right;

    public SidewaysGoal(GameWorld world, boolean right) {
        this.world = world;
        this.right = right;
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);
        addAction(new CenterAction(right));
        addAction(new DigForwardAction(world));
        addAction(new BuildAction(world, right, true));
        addAction(new MoveForwardAction(right));
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
