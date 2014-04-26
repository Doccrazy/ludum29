package de.doccrazy.ld29.game.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.base.Box2dActor;
import de.doccrazy.ld29.game.level.Category;
import de.doccrazy.ld29.game.level.Level;

public class LootActor extends Box2dActor {
    public static final float RADIUS = 0.1f;
    private float stateTime = 0f;
    private int type;
    private boolean dead;

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
        bodyDef.angularDamping = 0.5f;
        bodyDef.linearVelocity.x = 20f * ((float)Math.random() - 0.5f);
        bodyDef.linearVelocity.y = 20f * ((float)Math.random() - 0.5f);

        this.body = world.box2dWorld.createBody(bodyDef);
        this.body.setUserData(this);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = circle;
        fixDef.filter.categoryBits = Category.LOOT;
        fixDef.friction = 20f;
        fixDef.restitution = 0f;
        body.createFixture(fixDef);

        circle.dispose();

        // generate bob's actor
        this.setPosition(body.getPosition().x-RADIUS, body.getPosition().y-RADIUS); // set the actor position at the box2d body position
        this.setSize(RADIUS*2, RADIUS*2); // scale actor to body's size
        //this.setScaling(Scaling.stretch); // stretch the texture
        //this.setAlign(Align.center);

        type = (int) (Math.random() * Resource.loot.size);
    }

    @Override
    public void act(float delta) {
        if (dead) {
            remove();
            return;
        }
        super.act(delta);
        stateTime += delta;

        if (stateTime > 0.25f &&
                world.getCurrentLevel().getLevel().tileAt(Level.getTileIndex(getX() + RADIUS, getY() + RADIUS)) != null) {
            remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setOrigin(RADIUS, RADIUS);
        setRotation(MathUtils.radiansToDegrees * body.getAngle());
        setPosition(body.getPosition().x-RADIUS, body.getPosition().y-RADIUS);

        Matrix4 mat = batch.getTransformMatrix();
        Matrix4 old = mat.cpy();
        mat.trn(getX() + getOriginX(), getY() + getOriginY(), 0);
        batch.setTransformMatrix(mat);
        TextureRegion frame = Resource.loot.get(type);
        batch.draw(frame, -getOriginX(), -getOriginY(), 0, 0,
                RADIUS*5, RADIUS*5, getScaleX(), getScaleY(), 0);
        batch.setTransformMatrix(old);
    }

    public void kill() {
        dead = true;
    }

}
