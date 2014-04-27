package de.doccrazy.ld29.game.actor;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import box2dLight.Light;
import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.base.RegularAction;
import de.doccrazy.ld29.game.level.Category;
import de.doccrazy.ld29.game.level.Level;
import de.doccrazy.ld29.game.level.TileType;

public class LevelActor extends Actor {
    private Level level;
    GameWorld world;
    private Map<Point, Body> bodies = new HashMap<>();
    private Map<Body, Light> lights = new HashMap<>();

    public LevelActor(GameWorld world, Level level, int x) {
        this.world = world;
        this.level = level;
        setX(x);
        world.stage.addActor(this);
        initBodies();
        addAction(new UpdateTilesAction());
    }

    private void initBodies() {
        for (Entry<Point, TileType> entry : level.getMap().entrySet()) {
            createBodyForTile(entry.getKey(), entry.getValue());
        }
    }

    void createBodyForTile(Point pos, TileType value) {
        BodyDef tileBodyDef = new BodyDef();
        // Set its world position
        tileBodyDef.position.set(new Vector2(getX() + pos.x + 0.5f, getY() + pos.y + 0.5f));

        Body body = world.box2dWorld.createBody(tileBodyDef);
        body.setUserData(this);

        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(0.5f, 0.5f);
        FixtureDef def = new FixtureDef();
        def.shape = groundBox;
        def.density = 0f;
        def.filter.categoryBits = Category.LEVEL;
        // Create a fixture from our polygon shape and add it to our ground body
        body.createFixture(def);
        // Clean up after ourselves
        groundBox.dispose();

        bodies.put(pos, body);

        if (value == TileType.LAVA) {
            PointLight light = new PointLight(world.rayHandler, 5, new Color(1.0f, 0.3f, 0, 0.5f), 2.5f, body.getPosition().x, body.getPosition().y);
            light.setXray(true);
            lights.put(body, light);
        }
    }

    public void clearTile(Point pos) {
        level.clear(pos);
        if (bodies.containsKey(pos)) {
            Body body = bodies.get(pos);
            if (lights.containsKey(body)) {
                lights.get(body).remove();
                lights.remove(body);
            }
            world.box2dWorld.destroyBody(body);
            bodies.remove(pos);
        }
    }

    public void putTile(Point pos, TileType type) {
        clearTile(pos);
        level.put(pos, type);
        createBodyForTile(pos, type);
    }

    public boolean pickaxe(DiggerActor diggerActor, Point pos, float strength) {
        Float hp = level.healthAt(pos);
        if (hp != null && hp > 0) {
            //Resource.pickaxe.play();
            hp = hp - strength;
            level.put(pos, hp);
            if (hp <= 0) {
                diggerActor.onMine(level.tileAt(pos));
                clearTile(pos);
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (Entry<Point, TileType> entry : level.getMap().entrySet()) {
            Sprite sprite = Resource.tiles.get(entry.getValue());
            Point point = entry.getKey();
            if (sprite != null) {
                batch.draw(sprite, getX() + point.x + getOriginX(), getY() + point.y + getOriginY(), 0, 0, 1, 1, 1, 1, 0);
            }
        }
    }

    public Level getLevel() {
        return level;
    }

    public Point getTileIndex(Vector2 coord) {
        return getTileIndex(coord.x, coord.y);
    }

    public Point getTileIndex(float x, float y) {
        return new Point((int)Math.floor(x - getX()), (int)Math.floor(y - getY()));
    }

    public Vector2 tileToWorld(Point pos) {
        return new Vector2(pos.x + getX() + 0.5f, pos.y + getY() + 0.5f);
    }
}

class UpdateTilesAction extends RegularAction {
    private Level level;

    public UpdateTilesAction() {
        super(1f);
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);
        if (actor != null) {
            this.level = ((LevelActor)actor).getLevel();
        }
    }

    @Override
    protected boolean run(float delta) {
        update();
        return false;
    }

    public void update() {
        LevelActor levelActor = ((LevelActor)actor);
        HashMap<Point, TileType> oldMap = new HashMap<>(level.getMap());
        for (Entry<Point, TileType> entry : oldMap.entrySet()) {
            Point below = new Point(entry.getKey().x, entry.getKey().y - 1);
            Point right = new Point(entry.getKey().x + 1, entry.getKey().y);
            Point left = new Point(entry.getKey().x - 1, entry.getKey().y);
            if ((entry.getValue() == TileType.GRAVEL || entry.getValue() == TileType.SAND)
                    && level.tileAt(below) == null) {
                level.put(below, entry.getValue());
                levelActor.createBodyForTile(below, entry.getValue());
                level.put(below, level.healthAt(entry.getKey()));
                levelActor.clearTile(entry.getKey());
            }
            if (entry.getValue() == TileType.LAVA &&
                    (level.tileAt(below) == null || level.tileAt(left) == null || level.tileAt(right) == null)) {
                levelActor.clearTile(entry.getKey());
                for (int i = 0; i < 20; i++) {
                    new LavaballActor(levelActor.world, levelActor.tileToWorld(entry.getKey()));
                }
            }
        }
    }
}
