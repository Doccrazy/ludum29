package de.doccrazy.ld29.game.actor.ai;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.doccrazy.ld29.game.actor.DiggerActor;

public class CenterAction extends Action {
    private float xTarget;
    protected double threshold = 0.2;
    private Boolean orientRight;

    public CenterAction(Boolean orientRight) {
        this.orientRight = orientRight;
    }

    @Override
    public void setActor(Actor actor) {
        if (actor == null) {
            ((DiggerActor)getActor()).setMovement(0);
        }
        super.setActor(actor);
        restart();
    }

    @Override
    public boolean act(float delta) {
        if (!((DiggerActor)getActor()).touchingFloor()) {
            return false;
        }

        double dist = xTarget - (getActor().getX() + getActor().getWidth()/2);
        if (dist < -threshold) {
            ((DiggerActor)getActor()).setMovement(-1);
        } else if (dist > threshold) {
            ((DiggerActor)getActor()).setMovement(1);
        } else {
            ((DiggerActor)getActor()).setMovement(0);
            if (Boolean.TRUE.equals(orientRight)) {
                ((DiggerActor)getActor()).setOrientation(1);
            } else if (Boolean.FALSE.equals(orientRight)) {
                ((DiggerActor)getActor()).setOrientation(-1);
            }
            return true;
        }
        return false;
    }

    protected float getTargetPos() {
        return Math.round(getActor().getX() + getActor().getWidth()/2 - 0.5f) + 0.5f;
    }

    @Override
    public void restart() {
        super.restart();
        if (actor != null) {
            xTarget = getTargetPos();
        }
    }

}
