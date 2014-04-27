package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld29.game.GameWorld;
import de.doccrazy.ld29.game.actor.FloatingTextActor;

public class FloatingTextLabel extends Label {
    private GameWorld world;
    private float stateTime = 0f;

    public FloatingTextLabel(Stage stage, FloatingTextActor actor) {
        super("", new LabelStyle(new BitmapFont(), new Color(1f, 0.7f, 0f, 0.75f)));
        stage.addActor(this);
        this.world = actor.getWorld();
        setAlignment(Align.center);
        setText(actor.getText());
        Vector2 pos = world.stage.stageToScreenCoordinates(new Vector2(actor.getX(), actor.getY()));
        pos = getStage().screenToStageCoordinates(pos);
        setPosition(pos.x, pos.y);
        actor.remove();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setY(getY() + delta*20f);
        stateTime += delta;
        if (stateTime > 2f) {
            remove();
        }
    }
}
