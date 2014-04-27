package de.doccrazy.ld29.game.actor.ai;

public class MoveForwardAction extends CenterAction {
    private boolean right;

    public MoveForwardAction(boolean right) {
        this.right = right;
        threshold = 0.5;
    }

    @Override
    protected float getTargetPos() {
        return Math.round(getActor().getX() + getActor().getWidth()/2 - 0.5f) + 0.5f + (right ? 1 : -1);
    }
}
