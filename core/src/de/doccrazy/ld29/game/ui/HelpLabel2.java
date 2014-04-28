package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld29.core.Resource;

public class HelpLabel2 extends Label {
    private float stateTime = 0;

    public HelpLabel2() {
        super("", new LabelStyle(Resource.fontSmall, new Color(1f, 0.4f, 0.3f, 0.7f)));

        setAlignment(Align.center);
        setText("You cannot place blocks on ores or in the open.");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setPosition(0, getStage().getHeight() * 0.5f);
        setWidth(getStage().getWidth());

        stateTime += delta;
        if (stateTime > 5f) {
            remove();
        }
    }
}
