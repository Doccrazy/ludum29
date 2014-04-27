package de.doccrazy.ld29.game.actor.ai;

import java.awt.Point;

import com.badlogic.gdx.scenes.scene2d.Actor;

import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.actor.DiggerActor;
import de.doccrazy.ld29.game.actor.Tool;
import de.doccrazy.ld29.game.base.RegularAction;

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
            setDelay(((DiggerActor)actor).getHackDelay());
        }
    }

    @Override
    protected boolean init() {
        float orientation = ((DiggerActor)getActor()).getOrientation();
        Point pos1 = world.getCurrentLevel().getTileIndex(getActor().getX() + 0.5f + orientation, getActor().getY() + 0.5f);
        Point pos2 = world.getCurrentLevel().getTileIndex(getActor().getX() + 0.5f + orientation, getActor().getY() + 1.5f);
        if (world.getCurrentLevel().getLevel().tileAt(pos1) == null
                && world.getCurrentLevel().getLevel().tileAt(pos2) == null) {
            return true;
        }
        ((DiggerActor)actor).setTool(Tool.PICKAXE);
        return false;
    }

    @Override
    protected void done() {
        ((DiggerActor)actor).setTool(null);
    }

    @Override
    protected boolean run(float delta) {
        float orientation = ((DiggerActor)getActor()).getOrientation();
        Point pos1 = world.getCurrentLevel().getTileIndex(getActor().getX() + 0.5f + orientation, getActor().getY() + 0.5f);
        Point pos2 = world.getCurrentLevel().getTileIndex(getActor().getX() + 0.5f + orientation, getActor().getY() + 1.5f);
        if (world.getCurrentLevel().getLevel().tileAt(pos1) != null) {
            world.getCurrentLevel().pickaxe(pos1, ((DiggerActor)actor).getHackDamage());
        } else if (world.getCurrentLevel().getLevel().tileAt(pos2) != null) {
            world.getCurrentLevel().pickaxe(pos2, ((DiggerActor)actor).getHackDamage());
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
