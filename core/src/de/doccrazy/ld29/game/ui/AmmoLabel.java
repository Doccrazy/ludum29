package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld29.game.level.TileType;

public class AmmoLabel extends Label {
    private UiRoot root;
    private TileType type;

    public AmmoLabel(UiRoot root, TileType type) {
        super("", new LabelStyle(new BitmapFont(), new Color(0.5f, 0.5f, 1f, 1f)));
        this.root = root;
        this.type = type;

        //setFillParent(true);
        setAlignment(Align.right | Align.bottom);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Integer a = root.getInput().getAmmo().get(type);
        setText(a == null ? "" : a.toString());
    }
}
