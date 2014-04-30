package de.doccrazy.ld29.game.actor.ai;

import java.awt.Point;

import com.badlogic.gdx.scenes.scene2d.Actor;

import de.doccrazy.ld29.game.actor.DiggerActor;
import de.doccrazy.ld29.game.actor.Tool;
import de.doccrazy.ld29.game.base.RegularAction;
import de.doccrazy.ld29.game.world.GameWorld;

public class DigDownAction extends RegularAction {
    private GameWorld world;
    private boolean done;
	private boolean before;

    public DigDownAction(GameWorld world, boolean before) {
        super(1f);
        this.world = world;
		this.before = before;
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
        ((DiggerActor)actor).setTool(Tool.PICKAXE);
        return false;
    }

    @Override
    protected void done() {
        ((DiggerActor)actor).setTool(null);
        done = false;
    }

    @Override
    protected boolean run(float delta) {
    	float orientation = ((DiggerActor)getActor()).getOrientation();
        Point pos = world.getCurrentLevel().getTileIndex(getActor().getX() + 0.5f 
        		+ (before ? orientation: 0), getActor().getY() - 0.5f);
        if (done || world.getCurrentLevel().getLevel().tileAt(pos) == null) {
            return true;
        }
        done = world.getCurrentLevel().pickaxe((DiggerActor)getActor(), pos, ((DiggerActor)actor).getHackDamage());
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(getClass());
    }

}
