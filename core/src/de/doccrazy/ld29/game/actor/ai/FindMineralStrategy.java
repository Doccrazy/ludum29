package de.doccrazy.ld29.game.actor.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.actor.DiggerActor;
import de.doccrazy.ld29.game.level.TileType;

public class FindMineralStrategy extends SequenceAction {
    private GameWorld world;
    private Point target;
    private TileType mineral;

    public FindMineralStrategy(GameWorld world) {
        this.world = world;
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);

        if (actor != null) {
            restart();
        }
    }

    @Override
    public boolean act(float delta) {
        super.act(delta);
        if (target == null) {
            return true;
        }
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
            restart();
        }
        return false;
    }

    private Point findTargetPoint(TileType type) {
        Point diggerPos = world.getCurrentLevel().getTileIndex(getActor().getX() + 0.5f, getActor().getY() + 0.5f);
        List<Point> candidates = new ArrayList<>();
        Point pos = new Point();
        for (int y = diggerPos.y; y > -world.getCurrentLevel().getLevel().getHeight(); y--) {
            pos.y = y;
            for (int x = 0; x < world.getCurrentLevel().getLevel().getWidth(); x++) {
                pos.x = x;
                if (world.getCurrentLevel().getLevel().tileAt(pos) == type) {
                    candidates.add(new Point(pos));
                }
            }
        }
        if (candidates.size() > 0) {
            return candidates.get((int) (Math.random()*candidates.size()));
        }
        return null;
    }

    @Override
    public void restart() {
        TileType atTarget = world.getCurrentLevel().getLevel().tileAt(target);
        TileType required = ((DiggerActor)getActor()).requiredMineral();
        if (atTarget != required) {
            mineral = required;
            target = findTargetPoint(mineral);
        }
        super.restart();
    }

    @Override
    public void reset() {
        for (Action a : getActions()) {
            a.setActor(null);
        }
        super.reset();
    }

}
