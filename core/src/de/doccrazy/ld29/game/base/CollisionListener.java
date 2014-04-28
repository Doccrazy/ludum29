package de.doccrazy.ld29.game.base;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public interface CollisionListener {
	void beginContact(Body me, Body other, Vector2 normal, Vector2 contactPoint);

	void endContact(Body other);

	void hit(float force);
}
