package de.doccrazy.ld29.game.ui;

import java.awt.Point;

import org.apache.commons.lang3.ArrayUtils;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.level.Level;
import de.doccrazy.ld29.game.level.TileType;

public class GameInputListener extends InputListener {
    private GameWorld world;
    private TileType currentTile;
    private boolean enabled, selected;
    private int misclickCount = 0;
    private UiRoot root;

    public GameInputListener(UiRoot root) {
        this.root = root;
        this.world = root.getWorld();
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (!enabled) {
            return false;
        }
        if (!selected) {
            misclickCount++;
            if (misclickCount == 4) {
                root.getStage().addActor(new HelpLabel());
            }
            return false;
        }
        if (button == 0) {
            Level level = world.getCurrentLevel().getLevel();
            Point tilePos = world.getCurrentLevel().getTileIndex(x, y);
            Point above = new Point(tilePos.x, tilePos.y + 1);
            Point below = new Point(tilePos.x, tilePos.y - 1);
            Point left = new Point(tilePos.x - 1, tilePos.y);
            Point right = new Point(tilePos.x + 1, tilePos.y);
            if (currentTile == null) {
                if (!ArrayUtils.contains(Level.ORES, level.tileAt(tilePos))
                        && level.tileAt(left) != TileType.LAVA
                        && level.tileAt(right) != TileType.LAVA
                        && level.tileAt(above) != TileType.LAVA) {
                    world.getCurrentLevel().clearTile(tilePos);
                    return true;
                }
            } else if (level.tileAt(left) != null && level.tileAt(right) != null && level.tileAt(below) != null
                    && !ArrayUtils.contains(Level.ORES, level.tileAt(tilePos))) {
                world.getCurrentLevel().putTile(tilePos, currentTile);
                return true;
            }
            Resource.errorSound.play();
        }
        if (button == 1) {
            //world.spawnDigger(new Vector2(x, y));
            return true;
        }
        return false;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCurrentTile(TileType currentTile) {
        this.currentTile = currentTile;
        selected = true;
    }

}
