package de.doccrazy.ld29.game.actor;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.level.Level;
import de.doccrazy.ld29.game.level.TileType;

public class LevelActor extends Actor {
    private Level level;
    private GameWorld world;
    private Map<Point, Body> bodies = new HashMap<>();

    public LevelActor(GameWorld world, Level level) {
        this.world = world;
        this.level = level;
        world.stage.addActor(this);
        initBodies();
    }

    private void initBodies() {
        for (Entry<Point, TileType> entry : level.getMap().entrySet()) {
            Point pos = entry.getKey();
            BodyDef tileBodyDef = new BodyDef();
            // Set its world position
            tileBodyDef.position.set(new Vector2(pos.x, pos.y));

            Body body = world.box2dWorld.createBody(tileBodyDef);
            body.setUserData(this);

            // Create a polygon shape
            PolygonShape groundBox = new PolygonShape();
            // Set the polygon shape as a box which is twice the size of our view port and 20 high
            // (setAsBox takes half-width and half-height as arguments)
            groundBox.setAsBox(1, 1);
            // Create a fixture from our polygon shape and add it to our ground body
            body.createFixture(groundBox, 0.0f);
            // Clean up after ourselves
            groundBox.dispose();

            bodies.put(pos, body);
        }
    }

    public void clearTile(int x, int y) {
        level.clear(x, y);
        Point key = level.mapKey(x, y);
        if (bodies.containsKey(key)) {
            world.box2dWorld.destroyBody(bodies.get(key));
            bodies.remove(key);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (Entry<Point, TileType> entry : level.getMap().entrySet()) {
            Sprite sprite = Resource.tiles.get(entry.getValue());
            Point point = entry.getKey();
            if (sprite != null) {
                batch.draw(sprite, point.x + getOriginX(), point.y + getOriginY(),0, 0, 1, 1, 1, 1, 0);
            }
        }
    }

    public Level getLevel() {
        return level;
    }
}
