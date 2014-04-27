package de.doccrazy.ld29.game.actor.ai;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import de.doccrazy.ld29.game.GameWorld;

public class DownGoal extends SequenceAction {
    private GameWorld world;

    public DownGoal(GameWorld world) {
        this.world = world;
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);
        addAction(new CenterAction(null));
        addAction(new DigDownAction(world));
    }

    @Override
    public boolean act(float delta) {
        if (super.act(delta)) {
            restart();
        }
        return false;
    }
}
