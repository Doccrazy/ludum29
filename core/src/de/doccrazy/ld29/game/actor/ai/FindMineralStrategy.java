package de.doccrazy.ld29.game.actor.ai;

import java.awt.Point;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.level.TileType;

public class FindMineralStrategy extends SequenceAction {
    private GameWorld world;
    private Point target;

    public FindMineralStrategy(GameWorld world) {
        this.world = world;
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);

        target = findTargetPoint(TileType.IRON);
    }

    @Override
    public boolean act(float delta) {
        super.act(delta);
        Action current = getActions().size > 0 ? getActions().get(0) : null;
        Point pos = world.getCurrentLevel().getTileIndex(getActor().getX() + 0.5f, getActor().getY() + 0.5f);
        int deltax = target.x - pos.x;
        int deltay = target.y - pos.y;
        if (!(current instanceof SidewaysGoal || current instanceof DownGoal) && deltay < 0) {
            reset();
            if (Math.random() < 0.5 && deltax != 0) {
                addAction(new SidewaysGoal(world, deltax > 0));
            } else {
                addAction(new DownGoal(world));
            }
            restart();
        } else if (!(current instanceof StraightGoal) && deltay == 0 && deltax != 0) {
            reset();
            addAction(new StraightGoal(world, deltax > 0));
            restart();
        } else if (deltax == 0 && deltay == 0) {
            return true;
        }
        return false;
    }

    private Point findTargetPoint(TileType type) {
        Point pos = new Point();
        for (int y = 0; y > -world.getCurrentLevel().getLevel().getHeight(); y--) {
            pos.y = y;
            for (int x = 0; x < world.getCurrentLevel().getLevel().getWidth(); x++) {
                pos.x = x;
                if (world.getCurrentLevel().getLevel().tileAt(pos) == type) {
                    return pos;
                }
            }
        }
        return null;
    }

    @Override
    public void reset() {
        for (Action a : getActions()) {
            a.setActor(null);
        }
        super.reset();
    }

}
