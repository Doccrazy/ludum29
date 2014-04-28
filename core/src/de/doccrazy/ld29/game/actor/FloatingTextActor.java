package de.doccrazy.ld29.game.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;

import de.doccrazy.ld29.game.world.GameWorld;

public class FloatingTextActor extends Actor {
    private GameWorld world;
    private String text;
    private boolean rendered = false;

    public FloatingTextActor(GameWorld world, String text, float x, float y) {
        this.world = world;
        this.text = text;
        world.stage.addActor(this);
        setPosition(x, y);
    }

    public GameWorld getWorld() {
        return world;
    }

    public String getText() {
        return text;
    }

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }
}
