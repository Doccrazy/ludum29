package de.doccrazy.ld29.game.level;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Level {
    private Map<Point, TileType> map = new HashMap<>();
    private int width;
    private int height;

    private Point lookupKey = new Point();

    public Level(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TileType tileAt(int x, int y) {
        return map.get(mapKey(x, y));
    }

    public void put(int x, int y, TileType type) {
        Point key = new Point(x, y);
        map.put(key, type);
    }

    public void clear(int x, int y) {
        Point key = new Point(x, y);
        map.remove(key);
    }

    public Map<Point, TileType> getMap() {
        return map;
    }

    public Point mapKey(int x, int y) {
        lookupKey.x = x;
        lookupKey.y = y;
        return lookupKey;
    }

    public void random() {
        map.clear();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y > -height; y--) {
                put(x, y, pick(x, y));
            }
        }
    }

    private TileType pick(int x, int y) {
        float total = 0;
        Map<TileType, Float> probs = new LinkedHashMap<>();
        for (TileType type : TileType.values()) {
            float prob = prob(x, y, type);
            total += prob;
            probs.put(type, total);
        }
        float roll = (float) (Math.random() * total);
        for (Entry<TileType, Float> entry : probs.entrySet()) {
            if (roll < entry.getValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    private float prob(int x, int y, TileType type) {
        float result = 0;
        if (y == 0) {
            result = type == TileType.GRASS ? 1 : 0;
            return result;
        }
        switch (type) {
        case DIRT:
            result = y / (float)height + 1;
            break;
        case ROCK:
            result = -y / height + 0.5f;
            break;
        case GRAVEL:
            result = 0.3f * -y / height;
            break;
        case SAND:
            result = 0.2f * (y / height + 1);
            break;
        default:
        }
        if (tileAt(x+1, y) == type || tileAt(x-1, y) == type || tileAt(x, y+1) == type || tileAt(x, y-1) == type) {
            result = result * 5f;
        }
        return result;
    }
}
