package de.doccrazy.ld29.game.ui;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.level.TileType;
import de.doccrazy.ld29.game.world.GameState;

public class Toolbar extends Table {
    private UiRoot root;

    public Toolbar(UiRoot r) {
        this.root = r;
        pad(5);
        addTileButton(TileType.LAVA);
        addTileButton(TileType.SAND);
        addTileButton(null);
    }

    private void addTileButton(final TileType type) {
        Button b = newToolButton(type == null ? Resource.buttonClear : Resource.tiles.get(type));
        b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                root.getInput().setCurrentTile(type);
            }
        });
        b.add(new AmmoLabel(root, type));
    }

    private Button newToolButton(Sprite img) {
        Button b = new Button(new SpriteDrawable(img));
        add(b).width(32).height(32).expand().space(5);
        return b;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setVisible(root.getWorld().getGameState() == GameState.GAME);
        for (Iterator<Integer> it = root.getWorld().getDeathList().iterator(); it.hasNext(); ) {
            Integer level = it.next();
            root.getInput().addAmmo(TileType.LAVA, 1);
            root.getInput().addAmmo(TileType.SAND, 1);
            root.getInput().addAmmo(null, 2);
            root.getStage().addActor(new FloatingTextLabel(root.getStage(), "+" + (1) + " +" + (1) + " +" + (2),
                    new Vector2(30f, root.getStage().getHeight() - 60f), -1));
            it.remove();
        }
    }
}
