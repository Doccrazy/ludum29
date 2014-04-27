package de.doccrazy.ld29.game.actor;

import box2dLight.Light;
import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.base.Box2dActor;
import de.doccrazy.ld29.game.level.Category;

public class LavaballActor extends Box2dActor {
    public static final float RADIUS = 0.1f;

    public LavaballActor(GameWorld world, Vector2 spawn) {
        super(world);

        // generate bob's box2d body
        CircleShape circle = new CircleShape();
        circle.setRadius(RADIUS);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.x = spawn.x + (float)Math.random() * 0.1f - 0.05f;
        bodyDef.position.y = spawn.y + RADIUS + 0.1f + (float)Math.random() * 0.1f - 0.05f;
        bodyDef.linearDamping = 0.1f;
        bodyDef.angularDamping = 0.5f;
        bodyDef.linearVelocity.x = 20f * ((float)Math.random() - 0.5f);
        bodyDef.linearVelocity.y = 20f * ((float)Math.random() - 0.5f);

        this.body = world.box2dWorld.createBody(bodyDef);
        this.body.setUserData(this);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = circle;
        fixDef.filter.categoryBits = Category.LAVA;
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

        Light light = new PointLight(world.rayHandler, 5, new Color(1f,0.3f,0f,0.65f), 0, 0, 0);
        light.setXray(true);
        lights.add(light);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (dead) {
            return;
        }

        if (stateTime > 5f || (stateTime > 0.25f &&
                world.getCurrentLevel().getLevel().tileAt(world.getCurrentLevel().getTileIndex(getX() + RADIUS, getY() + RADIUS)) != null)) {
            kill();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setOrigin(RADIUS, RADIUS);
        setRotation(MathUtils.radiansToDegrees * body.getAngle());
        setPosition(body.getPosition().x-RADIUS, body.getPosition().y-RADIUS);

        float cool = MathUtils.clamp(1 - stateTime/8f, 0, 1);
        lights.get(0).setDistance(cool * 0.75f);
        lights.get(0).setPosition(body.getPosition().x, body.getPosition().y);

        batch.setColor(cool, cool, cool, cool);
        batch.draw(Resource.lavaball, getX() - RADIUS, getY(), 0, 0,
                RADIUS*5, RADIUS*5, getScaleX(), getScaleY(), 0);
        batch.setColor(1f, 1f, 1f, 1f);
    }

}
