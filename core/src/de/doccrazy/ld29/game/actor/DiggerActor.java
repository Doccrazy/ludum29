package de.doccrazy.ld29.game.actor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.base.Box2dActor;
import de.doccrazy.ld29.game.base.CollisionListener;
import de.doccrazy.ld29.game.level.Category;
import de.doccrazy.ld29.game.level.Level;

public class DiggerActor extends Box2dActor implements CollisionListener {
    public static final float RADIUS = 0.4f;
    private static final int CONTACT_TTL = 50;
    private static final float VELOCITY = 10.f;

    private Map<Body, ContactInfo> floorContacts = new HashMap<Body, ContactInfo>();
    private float orientation = 1;
    private float movement = 0;
    private boolean digging = false;

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

        Fixture fix = body.createFixture(circle, 100);
        fix.setFriction(20f);
        fix.setRestitution(0f);
        //fix.setFilterData(filter);

        circle.setRadius(1.5f);
        FixtureDef sensor = new FixtureDef();
        sensor.shape = circle;
        sensor.isSensor = true;
        sensor.filter.categoryBits = Category.LOOT_SENSOR;
        sensor.filter.maskBits = Category.LOOT;
        body.createFixture(sensor);

        circle.dispose();

        // generate bob's actor
        this.setPosition(body.getPosition().x-RADIUS, body.getPosition().y-RADIUS); // set the actor position at the box2d body position
        this.setSize(RADIUS*2, RADIUS*2); // scale actor to body's size
        //this.setScaling(Scaling.stretch); // stretch the texture
        //this.setAlign(Align.center);

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

        processContacts();
        move(delta);

        if (world.getCurrentLevel().getLevel().tileAt(Level.getTileIndex(getX() + RADIUS, getY() + RADIUS)) != null) {
            kill();
        }
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

    private boolean touchingFloor() {
        return floorContacts.size() > 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setOrigin(RADIUS, RADIUS);
        setRotation(MathUtils.radiansToDegrees * body.getAngle());
        setPosition(body.getPosition().x-RADIUS, body.getPosition().y-RADIUS);

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
                RADIUS*2, RADIUS*3, getScaleX(), getScaleY(), 0);
        batch.setTransformMatrix(old);
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
            ((LootActor)other.getUserData()).kill();
        }
        if (normal.y > 0.707f && !other.getFixtureList().get(0).isSensor()) {   //45 deg
            addFloorContact(other, contactPoint);
        }
    }

    @Override
    public void endContact(Body other) {
        removeFloorContact(other);
    }

    public void kill() {
        Vector2 pos = getBody().getPosition();
        if (remove()) {
            for (int i = 0; i < ((int)20f * Math.random()); i++) {
                new LootActor(this.world, pos);
            }
            //Resource.die.play();
        }
    }

    public void setMovement(float movement) {
        this.movement = movement;
    }

    public float getOrientation() {
        return orientation;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
