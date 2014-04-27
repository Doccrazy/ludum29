package de.doccrazy.ld29.game.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.level.TileType;

public class Toolbar extends Table {
    private UiRoot root;

    public Toolbar(UiRoot r) {
        this.root = r;
        pad(5);
        addTileButton(TileType.LAVA);
        addTileButton(TileType.SAND);

        Button b = newToolButton(Resource.buttonClear);
        b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                root.getInput().setCurrentTile(null);
            }
        });
    }

    private void addTileButton(final TileType type) {
        Button b = newToolButton(Resource.tiles.get(type));
        b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                root.getInput().setCurrentTile(type);
            }
        });
    }

    private Button newToolButton(Sprite img) {
        Button b = new Button(new SpriteDrawable(img));
        add(b).width(32).height(32).expand().space(5);
        return b;
    }
}
