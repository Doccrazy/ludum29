package de.doccrazy.ld29.game.actor;

import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.base.Box2dActor;
import de.doccrazy.ld29.game.base.CollisionListener;
import de.doccrazy.ld29.game.level.Category;
import de.doccrazy.ld29.game.world.GameWorld;

public class LootActor extends Box2dActor implements CollisionListener {
    public static final float RADIUS = 0.1f;
    private int type;
    private PointLight light;

    public LootActor(GameWorld world, Vector2 spawn) {
        super(world);

        // generate bob's box2d body
        CircleShape circle = new CircleShape();
        circle.setRadius(RADIUS);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.x = spawn.x + (float)Math.random() * 0.1f - 0.05f;
        bodyDef.position.y = spawn.y + RADIUS + 0.1f + (float)Math.random() * 0.1f - 0.05f;
        bodyDef.linearDamping = 0.1f;
        bodyDef.angularDamping = 0.8f;
        bodyDef.linearVelocity.x = 20f * ((float)Math.random() - 0.5f);
        bodyDef.linearVelocity.y = 20f * ((float)Math.random() - 0.5f);

        this.body = world.box2dWorld.createBody(bodyDef);
        this.body.setUserData(this);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = circle;
        fixDef.filter.categoryBits = Category.LOOT;
        fixDef.friction = 3f;
        fixDef.restitution = 0.1f;
        fixDef.density = 1;
        body.createFixture(fixDef);

        circle.dispose();

        // generate bob's actor
        this.setPosition(body.getPosition().x-RADIUS, body.getPosition().y-RADIUS); // set the actor position at the box2d body position
        this.setSize(RADIUS*2, RADIUS*2); // scale actor to body's size
        //this.setScaling(Scaling.stretch); // stretch the texture
        //this.setAlign(Align.center);

        light = new PointLight(world.rayHandler, 5, new Color(1f,1f,1f,0.65f), 0.75f, 0, 0);
        light.setXray(true);

        type = (int) (Math.random() * Resource.loot.size);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (dead) {
            return;
        }

        if (stateTime > 0.25f &&
                world.getCurrentLevel().getLevel().tileAt(world.getCurrentLevel().getTileIndex(getX() + RADIUS, getY() + RADIUS)) != null) {
            kill();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setOrigin(RADIUS, RADIUS);
        setRotation(MathUtils.radiansToDegrees * body.getAngle());
        setPosition(body.getPosition().x-RADIUS, body.getPosition().y-RADIUS);
        light.setPosition(body.getPosition().x, body.getPosition().y);

        TextureRegion frame = Resource.loot.get(type);
        batch.draw(frame, getX() - RADIUS, getY(), 0, 0,
                RADIUS*5, RADIUS*5, getScaleX(), getScaleY(), 0);
    }

    @Override
    public void beginContact(Body me, Body other, Vector2 normal, Vector2 contactPoint) {
        if (other.getUserData() instanceof LavaballActor) {
            kill();
        }
    }

    @Override
    public void endContact(Body other) {
    }

    @Override
    public void hit(float force) {
    }

    @Override
    public boolean remove() {
        boolean ret = super.remove();
        if (ret) {
            light.remove();
        }
        return ret;
    }

}
