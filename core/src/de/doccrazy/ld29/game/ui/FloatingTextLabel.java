package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld29.game.actor.FloatingTextActor;

public class FloatingTextLabel extends Label {
    private float stateTime = 0f;
    private int direction;

    public FloatingTextLabel(Stage stage, String text, Vector2 pos, int direction) {
        super(text, new LabelStyle(new BitmapFont(), new Color(1f, 0.7f, 0f, 0.75f)));
        this.direction = direction;
        stage.addActor(this);
        setAlignment(Align.center);
        setPosition(pos.x, pos.y);
    }

    public FloatingTextLabel(Stage stage, FloatingTextActor actor) {
        this(stage, actor.getText(), new Vector2(), 1);
        Vector2 pos = actor.getWorld().stage.stageToScreenCoordinates(new Vector2(actor.getX(), actor.getY()));
        pos = getStage().screenToStageCoordinates(pos);
        setPosition(pos.x, pos.y);
        actor.remove();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setY(getY() + direction*delta*20f);
        stateTime += delta;
        if (stateTime > 2f) {
            remove();
        }
    }
}
