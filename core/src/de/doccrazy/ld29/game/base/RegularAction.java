package de.doccrazy.ld29.game.base;

import com.badlogic.gdx.scenes.scene2d.Action;

public abstract class RegularAction extends Action {
    private float delay;

    private float deltaCache = 0;
    private boolean enabled = true;

    public RegularAction(float delay) {
        this.delay = delay;
    }

    @Override
    public final boolean act(float delta) {
        if (!enabled) {
            return false;
        }
        deltaCache += delta;

        while (deltaCache >= delay) {
            deltaCache -= delay;
            if (run(delay)) {
                return true;
            }
        }
        return false;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    abstract protected boolean run(float delta);

}
