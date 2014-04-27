package de.doccrazy.ld29.game.actor.ai;

import java.awt.Point;

import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.base.RegularAction;
import de.doccrazy.ld29.game.level.TileType;

public class BuildAction extends RegularAction {
    private GameWorld world;
    private boolean right;

    public BuildAction(GameWorld world, boolean right) {
        super(0.5f);
        this.world = world;
        this.right = right;
    }

    @Override
    protected boolean init() {
        Point pos = world.getCurrentLevel().getTileIndex(getActor().getX() + 0.5f + (right ? 1 : -1), getActor().getY() - 0.5f);
        if (world.getCurrentLevel().getLevel().tileAt(pos) == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean run(float delta) {
        Point pos = world.getCurrentLevel().getTileIndex(getActor().getX() + 0.5f + (right ? 1 : -1), getActor().getY() - 0.5f);
        if (world.getCurrentLevel().getLevel().tileAt(pos) == null) {
            world.getCurrentLevel().putTile(pos, TileType.DIRT);
        }
        return true;
    }

}
