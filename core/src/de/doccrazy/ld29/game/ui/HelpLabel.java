package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld29.core.Main;
import de.doccrazy.ld29.core.Resource;

public class HelpLabel extends Label {
    private float stateTime = 0;

    public HelpLabel() {
        super(Main.GAME_TITLE, new LabelStyle(Resource.fontSmall, new Color(1f, 0.4f, 0.3f, 0.7f)));

        setFillParent(true);
        setAlignment(Align.center | Align.bottom);
        setText("Hint: Select your weapon of choice \nby clicking one of the options in the top left.");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        if (stateTime > 5f) {
            remove();
        }
    }
}
