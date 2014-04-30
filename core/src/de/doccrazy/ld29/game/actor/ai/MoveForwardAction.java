package de.doccrazy.ld29.game.actor.ai;

public class MoveForwardAction extends CenterAction {
    private boolean right;

    public MoveForwardAction(boolean right) {
    	this(right, 1);
    }
    
	public MoveForwardAction(boolean right, float speed) {
        super(null);
        this.right = right;
        this.speed = speed;
        threshold = speed/2f;
    }

    @Override
    protected float getTargetPos() {
        return Math.round(getActor().getX() + getActor().getWidth()/2 - 0.5f) + 0.5f + (right ? 1 : -1);
    }
}
