package de.doccrazy.ld29.game.actor;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.base.Box2dActor;
import de.doccrazy.ld29.game.base.CollisionListener;
import de.doccrazy.ld29.game.level.Category;
import de.doccrazy.ld29.game.level.Mask;
import de.doccrazy.ld29.game.level.TileType;
import de.doccrazy.ld29.game.world.GameWorld;

public class DiggerActor extends Box2dActor implements CollisionListener {
    public static final float RADIUS = 0.4f;
    private static final int CONTACT_TTL = 50;
    private static final float VELOCITY = 10.f;

    private Map<Body, ContactInfo> floorContacts = new HashMap<Body, ContactInfo>();
    private PointLight light;
    private float orientation = 1;
    private float movement = 0;
    private Mood mood;
    private float moodTimer;
    private Tool tool;
    private int level = 0;

    public DiggerActor(GameWorld w, Vector2 spawn) {
        super(w);

        // generate bob's box2d body
        CircleShape circle = new CircleShape();
        circle.setRadius(RADIUS);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.x = spawn.x;
        bodyDef.position.y = spawn.y + RADIUS + 0.1f;
        bodyDef.linearDamping = 0.1f;
        bodyDef.angularDamping = 0.5f;

        this.body = world.box2dWorld.createBody(bodyDef);
        this.body.setUserData(this);

        FixtureDef def = new FixtureDef();
        def.shape = circle;
        def.friction = 20f;
        def.restitution = 0f;
        def.density = 100;
        def.filter.maskBits = Mask.LEVEL_LOOT_LAVA;
        body.createFixture(def);

        circle.setRadius(1.5f);
        FixtureDef sensor = new FixtureDef();
        sensor.shape = circle;
        sensor.isSensor = true;
        sensor.filter.maskBits = Category.LOOT;
        body.createFixture(sensor);

        circle.dispose();

        // generate bob's actor
        this.setPosition(body.getPosition().x-RADIUS, body.getPosition().y-RADIUS); // set the actor position at the box2d body position
        this.setSize(RADIUS*2, RADIUS*2); // scale actor to body's size
        //this.setScaling(Scaling.stretch); // stretch the texture
        //this.setAlign(Align.center);

        light = new PointLight(world.rayHandler, 100, new Color(1f,0.4f,0.0f,0.7f), 1, 0, 0);
        light.setSoftnessLength(5);
        lights.add(light);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                kill();
                return false;
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (dead) {
            return;
        }

        stateTime += delta;
        processContacts();
        move(delta);

        moodTimer -= delta;
        if (moodTimer < 0) {
            mood = null;
        }

        if (world.getCurrentLevel().getLevel().tileAt(world.getCurrentLevel().getTileIndex(getX() + RADIUS, getY() + RADIUS)) != null) {
            kill();
        }
    }

    @Override
    protected void die() {
        Vector2 pos = getBody().getPosition();
        if (remove()) {
            for (int i = 0; i < (15f * Math.random() + 5f); i++) {
                new LootActor(this.world, pos);
            }
            world.diggerDie(this);
        }
        super.die();
    }

    private void processContacts() {
        for (Iterator<Entry<Body, ContactInfo>> it = floorContacts.entrySet().iterator(); it.hasNext(); ) {
            Entry<Body, ContactInfo> entry = it.next();
            entry.getValue().ttl -= 1;
            if (entry.getValue().ttl <= 0) {
                it.remove();
            }
        }
    }

    private void move(float delta) {
        if (movement < 0) {
            orientation = -1;
        } else if (movement > 0) {
            orientation = 1;
        }
        if (touchingFloor()) {
            body.setAngularVelocity(-movement*VELOCITY);
        }
    }

    public boolean touchingFloor() {
        return floorContacts.size() > 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setOrigin(RADIUS, RADIUS);
        setRotation(MathUtils.radiansToDegrees * body.getAngle());
        setPosition(body.getPosition().x-RADIUS, body.getPosition().y-RADIUS);
        light.setPosition(body.getPosition().x, body.getPosition().y + RADIUS*2f);
        light.setDistance((float)(7f + 1f*Math.sin(stateTime*20)));
        light.setActive(body.getPosition().y < -5);

        Matrix4 mat = batch.getTransformMatrix();
        Matrix4 old = mat.cpy();
        mat.trn(getX() + getOriginX(), getY() + getOriginY(), 0);
        mat.scale(orientation, 1, 1);
        batch.setTransformMatrix(mat);
        TextureRegion frame = Resource.digger;
        if (movement != 0) {
            //frame = Resource.playerWalk.getKeyFrame(stateTime, true);
        }
        batch.draw(frame, -getOriginX(), -getOriginY(), 0, 0,
                RADIUS*2, RADIUS*4, getScaleX(), getScaleY(), 0);

        if (tool != null) {
            batch.draw(Resource.tools.get(tool), -0.25f, -0.1f, 0.2f, 0.5f,
                    1f, 1f, getScaleX(), getScaleY(), ((-stateTime / getHackDelay()) % 1f) * 360f);
        } else {
            batch.draw(Resource.toolNone, -0.25f, -0.1f, 0.2f, 0.5f,
                    1f, 1f, getScaleX(), getScaleY(), -90 + (float)Math.sin(stateTime*3) * 30);
        }

        batch.setTransformMatrix(old);

        if (mood != null) {
            batch.draw(Resource.thoughtBubble, getX() + 2*getOriginX(), getY() + 2*getOriginY(), 0, 0,
                    2.5f, 2f, getScaleX(), getScaleY(), 0);
            batch.draw(Resource.moods.get(mood), getX() + 2*getOriginX() + 0.75f, getY() + 2*getOriginY() + 0.65f, 0, 0,
                    1.2f, 1.2f, getScaleX(), getScaleY(), 0);
        }
    }

    public void addFloorContact(Body body, Vector2 point) {
        floorContacts.put(body, new ContactInfo(Integer.MAX_VALUE, point));
    }

    public void removeFloorContact(Body body) {
        ContactInfo info = floorContacts.get(body);
        if (info != null) {
            info.ttl = CONTACT_TTL;
        }
    }

    private static class ContactInfo {
        private int ttl;
        private Vector2 p;
        ContactInfo(int ttl, Vector2 p) {
            this.ttl = ttl;
            this.p = p;
        }
    }

    @Override
    public void beginContact(Body me, Body other, Vector2 normal, Vector2 contactPoint) {
        if (other.getUserData() instanceof LootActor) {
            ((Box2dActor)other.getUserData()).kill();
            Resource.pickupLoot.play();
        }
        if (other.getUserData() instanceof LevelActor) {
            Point tile = world.getCurrentLevel().getTileIndex(other.getPosition());
            if (world.getCurrentLevel().getLevel().tileAt(tile) == TileType.LAVA) {
                kill();
            }
        }
        if (other.getUserData() instanceof LavaballActor) {
            kill();
        }
        if (normal.y > 0.707f && !other.getFixtureList().get(0).isSensor()) {   //45 deg
            addFloorContact(other, contactPoint);
        }
    }

    @Override
    public void endContact(Body other) {
        removeFloorContact(other);
    }

    @Override
    public void hit(float force) {
        if (force > 400) {
            kill();
        }
    }

    public void setMovement(float movement) {
        this.movement = movement;
    }

    public float getOrientation() {
        return orientation;
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getHackDelay() {
        return 0.5f - level * 0.025f;
    }

    public float getHackDamage() {
        return 1 + level * 0.5f;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood, float time) {
        this.mood = mood;
        this.moodTimer = time;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public void onMine(TileType type) {
        if (type == requiredMineral()) {
            new FloatingTextActor(world, "Level Up!", getX() + RADIUS, getY() + RADIUS*3);
            world.diggerLevelUp();
        }
    }

    public TileType requiredMineral() {
        return requiredMineral(level);
    }

    public static TileType requiredMineral(int level) {
        switch (level) {
        case 0: return TileType.COAL;
        case 1: return TileType.IRON;
        case 2: return TileType.SILVER;
        case 3: return TileType.GOLD;
        case 4: return TileType.DIAMOND;
        default:
        }
        return null;
    }
}
