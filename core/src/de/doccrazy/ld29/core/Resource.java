package de.doccrazy.ld29.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import de.doccrazy.ld29.game.level.TileType;

public class Resource {
	public static Texture backgroundLow, backgroundHigh;
	public static Map<TileType, Sprite> tiles = new HashMap<>();
	public static Sprite digger;

	private Resource() {
	}

	public static void init() {
		backgroundLow = new Texture(Gdx.files.internal("background-low.png"));
        backgroundHigh = new Texture(Gdx.files.internal("background-high.png"));

        TextureAtlas atlasGame = new TextureAtlas(Gdx.files.internal("game.atlas"));
        for (TileType type : TileType.values()) {
            Sprite sprite = atlasGame.createSprite(type.toString().toLowerCase());
            if (sprite == null) {
                Color c = new Color(type.toString().hashCode());
                c.a = 0.3f;
                sprite = colorSprite(c, 16, 16);
            }
            tiles.put(type, sprite);
        }

        digger = colorSprite(Color.RED, 16, 32);
	}

	private static Sprite colorSprite(Color color, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        return new Sprite(tex);
	}
}
