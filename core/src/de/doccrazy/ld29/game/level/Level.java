package de.doccrazy.ld29.game.level;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;

import com.badlogic.gdx.math.MathUtils;

public class Level {
    private Map<Point, TileType> map = new HashMap<>();
    private Map<Point, Float> health = new HashMap<>();
    private int width;
    private int height;

    private static final Map<TileType, Integer> MIN_DEPTH = new HashMap<TileType, Integer>() {{
        put(TileType.COAL, -2);
        put(TileType.IRON, -5);
        put(TileType.SILVER, -8);
        put(TileType.GOLD, -10);
        put(TileType.DIAMOND, -17);
    }};
    private static final Map<TileType, Integer> MAX_DEPTH = new HashMap<TileType, Integer>() {{
        put(TileType.COAL, -5);
        put(TileType.IRON, -12);
        put(TileType.SILVER, -12);
        put(TileType.GOLD, -15);
        put(TileType.DIAMOND, -22);
    }};
    private static final Map<TileType, Integer> INIT_HEALTH = new HashMap<TileType, Integer>() {{
        put(TileType.GRASS, 2);
        put(TileType.DIRT, 2);
        put(TileType.ROCK, 4);
        put(TileType.GRAVEL, 2);
        put(TileType.SAND, 2);
        put(TileType.OBSIDIAN, 40);
        put(TileType.COAL, 4);
        put(TileType.IRON, 6);
        put(TileType.SILVER, 8);
        put(TileType.GOLD, 10);
        put(TileType.DIAMOND, 16);
        put(TileType.WATER, 9999);
        put(TileType.LAVA, 9999);
    }};
    public static final TileType[] ORES = {TileType.COAL, TileType.IRON, TileType.SILVER, TileType.GOLD, TileType.DIAMOND};

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
        return tileAt(mapKey(x, y));
    }

    public TileType tileAt(Point pos) {
        return map.get(pos);
    }

    public Float healthAt(Point pos) {
        return health.get(pos);
    }

    public void put(Point pos, TileType type) {
        map.put(pos, type);
        health.put(pos, Float.valueOf(INIT_HEALTH.get(type)));
    }

    public void put(Point pos, Float hp) {
        health.put(pos, hp);
    }

    public void clear(Point pos) {
        map.remove(pos);
        health.remove(pos);
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
        health.clear();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y > -height; y--) {
                put(new Point(x, y), pick(x, y));
            }
        }
        int size = 10;
        for (TileType type : ORES) {
            for (int i = 0; i < 2; i++) {
                placeOre(type, size);
            }
            size--;
        }
    }

    private void placeOre(TileType type, int size) {
        Point pos = null;
        while (pos == null || ArrayUtils.contains(ORES, tileAt(pos))) {
            int y = (int) (MIN_DEPTH.get(type) + Math.random() * (MAX_DEPTH.get(type) - MIN_DEPTH.get(type)));
            pos = new Point((int) (Math.random() * width), y);
        }
        put(pos, type);
        extend(pos, size, -1);
    }

    private void extend(Point pos, int size, int nope) {
        TileType type = tileAt(pos);
        Point n = pos;
        int tries = 10;
        int r = -1;
        while (tileAt(n) == type) {
            r = RandomUtils.nextInt(0, 3);
            if (r == nope) {
                continue;
            }
            int x = r == 0 ? -1 : (r == 1 ? 1 : 0);
            int y = r == 2 ? -1 : 0;
            n = new Point(pos.x + x, pos.y + y);
            tries--;
            if (tries == 0) {
                return;
            }
        }
        put(n, type);
        if (size > 1) {
            extend(n, size-1, r);
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
        if (x == 0 || x == width - 1 || y == -height + 1) {
            result = type == TileType.OBSIDIAN ? 1 : 0;
            return result;
        }
        float depth = -y / (float)height;
        switch (type) {
        case DIRT:
            result = 1 - depth;
            break;
        case ROCK:
            result = depth + 0.5f;
            break;
        case GRAVEL:
            result = 0.3f * depth;
            break;
        case SAND:
            result = 0.2f * (1 - depth);
            break;
        case LAVA:
            result = MathUtils.clamp(depth - 0.5f, 0, 1);
            break;
        default:
        }
        if (tileAt(x+1, y) == type || tileAt(x-1, y) == type || tileAt(x, y+1) == type || tileAt(x, y-1) == type) {
            result = result * 5f;
        }
        return result;
    }

}
