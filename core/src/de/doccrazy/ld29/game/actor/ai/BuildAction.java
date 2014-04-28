package de.doccrazy.ld29.game.actor.ai;

import java.awt.Point;

import de.doccrazy.ld29.game.base.RegularAction;
import de.doccrazy.ld29.game.level.TileType;
import de.doccrazy.ld29.game.world.GameWorld;

public class BuildAction extends RegularAction {
    private GameWorld world;
    private boolean right;
    private boolean allowLava;

    public BuildAction(GameWorld world, boolean right, boolean allowLava) {
        super(0.5f);
        this.world = world;
        this.right = right;
        this.allowLava = allowLava;
    }

    @Override
    protected boolean init() {
        Point pos = world.getCurrentLevel().getTileIndex(getActor().getX() + 0.5f + (right ? 1 : -1), getActor().getY() - 0.5f);
        TileType tile = world.getCurrentLevel().getLevel().tileAt(pos);
        if (tile == null || (allowLava && tile == TileType.LAVA)) {
            return false;
        }
        return true;
    }

    @Override
    protected void done() {
    }

    @Override
    public boolean run(float delta) {
        Point pos = world.getCurrentLevel().getTileIndex(getActor().getX() + 0.5f + (right ? 1 : -1), getActor().getY() - 0.5f);
        TileType tile = world.getCurrentLevel().getLevel().tileAt(pos);
        if (tile == null || (allowLava && tile == TileType.LAVA)) {
            world.getCurrentLevel().putTile(pos, tile == TileType.LAVA ? TileType.OBSIDIAN : TileType.DIRT);
        }
        return true;
    }

}
