package de.doccrazy.ld29.game.actor;

import java.awt.Point;

import com.badlogic.gdx.scenes.scene2d.Actor;

import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.base.RegularAction;
import de.doccrazy.ld29.game.level.Level;

public class DigForwardAction extends RegularAction {
    private GameWorld world;

    public DigForwardAction(GameWorld world) {
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
        float orientation = ((DiggerActor)getActor()).getOrientation();
        Point pos1 = Level.getTileIndex(getActor().getX() + 0.5f + orientation, getActor().getY() + 0.5f);
        Point pos2 = Level.getTileIndex(getActor().getX() + 0.5f + orientation, getActor().getY() + 1.5f);
        if (world.getCurrentLevel().getLevel().tileAt(pos1) != null) {
            world.getCurrentLevel().pickaxe(pos1, 1 + ((DiggerActor)actor).getLevel() * 0.5f);
        } else if (world.getCurrentLevel().getLevel().tileAt(pos2) != null) {
            world.getCurrentLevel().pickaxe(pos2, 1 + ((DiggerActor)actor).getLevel() * 0.5f);
        } else {
            return true;
        }
        return world.getCurrentLevel().getLevel().tileAt(pos1) == null
                && world.getCurrentLevel().getLevel().tileAt(pos2) == null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(getClass());
    }

}
