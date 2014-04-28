package de.doccrazy.ld29.game.world;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.level.Level;
import de.doccrazy.ld29.game.level.TileType;
import de.doccrazy.ld29.game.ui.HelpLabel;
import de.doccrazy.ld29.game.ui.HelpLabel2;
import de.doccrazy.ld29.game.ui.UiRoot;

public class GameInputListener extends InputListener {
    private GameWorld world;
    private TileType currentTile;
    private boolean selected;
    private int misclickCount = 0, openCount = 0;
    private UiRoot root;
    private Map<TileType, Integer> ammo = new HashMap<>();

    public GameInputListener(UiRoot root) {
        this.root = root;
        this.world = root.getWorld();
        reset();
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (world.getGameState() != GameState.GAME) {
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
            if (ammo.get(currentTile) == null || ammo.get(currentTile) <= 0) {
                Resource.errorSound.play();
                return false;
            }
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
                    addAmmo(currentTile, -1);
                    return true;
                }
            } else if (level.tileAt(below) != null && !ArrayUtils.contains(Level.ORES, level.tileAt(tilePos))) {
                if (currentTile != TileType.LAVA || (level.tileAt(left) != null && level.tileAt(right) != null)) {
                    world.getCurrentLevel().putTile(tilePos, currentTile);
                    addAmmo(currentTile, -1);
                    return true;
                }
            }
            Resource.errorSound.play();
            openCount++;
            if (openCount == 1) {
                root.getStage().addActor(new HelpLabel2());
            }
        }
        if (button == 1) {
            //world.spawnDigger(new Vector2(x, y));
            return true;
        }
        return false;
    }

    public TileType getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(TileType currentTile) {
        this.currentTile = currentTile;
        selected = true;
    }

    public boolean isSelected() {
        return selected;
    }

    public Map<TileType, Integer> getAmmo() {
        return ammo;
    }

    public void addAmmo(TileType type, int amount) {
        ammo.put(type, ammo.get(type) + amount);
    }

    public void reset() {
        ammo.clear();
        ammo.put(TileType.LAVA, 2);
        ammo.put(TileType.SAND, 4);
        ammo.put(null, 10);
    }
}
