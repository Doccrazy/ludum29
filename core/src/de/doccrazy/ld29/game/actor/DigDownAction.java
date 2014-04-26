package de.doccrazy.ld29.game.actor;

import java.awt.Point;

import com.badlogic.gdx.scenes.scene2d.Actor;

import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.base.RegularAction;
import de.doccrazy.ld29.game.level.Level;

public class DigDownAction extends RegularAction {
    private GameWorld world;

    public DigDownAction(GameWorld world) {
        super(1f);
        this.world = world;
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);
        if (actor != null) {
            setDelay(1f - ((DiggerActor)actor).getLevel() * 0.1f);
        }
    }

    @Override
    protected boolean run(float delta) {
        Point pos = Level.getTileIndex(getActor().getX() + 0.5f, getActor().getY() - 0.5f);
        if (world.getCurrentLevel().getLevel().tileAt(pos) != null) {
            world.getCurrentLevel().pickaxe(pos, 1 + ((DiggerActor)actor).getLevel() * 0.5f);
            return world.getCurrentLevel().getLevel().tileAt(pos) == null;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(getClass());
    }

}
