package de.doccrazy.ld29.game.actor.ai;

import de.doccrazy.ld29.game.world.GameRules;

public class InitialWalkAction extends CenterAction {
    private float target;

    public InitialWalkAction() {
        super(null);
        target = Math.round(2.5f + Math.random() * (GameRules.LEVEL_WIDTH - 5) - 0.5f) + 0.5f;
    }

    @Override
    protected float getTargetPos() {
        return target;
    }
}
