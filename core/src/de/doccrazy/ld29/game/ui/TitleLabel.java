package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld29.core.Main;
import de.doccrazy.ld29.core.Resource;

public class TitleLabel extends Label {
    public TitleLabel() {
        super(Main.GAME_TITLE, new LabelStyle(Resource.fontBig, new Color(1f, 0.4f, 0.3f, 0.7f)));

        setAlignment(Align.center);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getStage() != null) {
            setPosition(0, getStage().getHeight() * 0.7f);
            setWidth(getStage().getWidth());
        }
    }
}
