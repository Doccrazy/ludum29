package de.doccrazy.ld29.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

import de.doccrazy.ld29.game.actor.Mood;
import de.doccrazy.ld29.game.actor.Tool;
import de.doccrazy.ld29.game.level.TileType;

public class Resource {
    public static Texture backgroundLow, backgroundHigh;
    public static Map<TileType, Sprite> tiles = new HashMap<>();
    public static Sprite digger;
    public static Array<AtlasRegion> loot;
    public static Sprite thoughtBubble;
    public static Map<Mood, Sprite> moods = new HashMap<>();
    public static Map<Tool, Sprite> tools = new HashMap<>();
    public static Sprite toolNone;
    public static Sprite lavaball;

    public static Sound pickaxe;
    public static Sound die;
    public static Sound pickupLoot;

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
                switch (type) {
                case COAL:
                    c = Color.BLACK;
                    break;
                case IRON:
                    c = Color.ORANGE;
                    break;
                case SILVER:
                    c = Color.LIGHT_GRAY;
                    break;
                case GOLD:
                    c = Color.YELLOW;
                    break;
                case DIAMOND:
                    c = Color.CYAN;
                    break;
                default:
                }
                sprite = colorSprite(c, 16, 16);
            }
            tiles.put(type, sprite);
        }

        digger = atlasGame.createSprite("guy");
        loot = atlasGame.findRegions("loot");
        thoughtBubble = atlasGame.createSprite("thoughtBubble");
        for (Mood mood : Mood.values()) {
            moods.put(mood, atlasGame.createSprite("mood-" + mood.toString().toLowerCase()));
        }
        for (Tool tool : Tool.values()) {
            tools.put(tool, atlasGame.createSprite("tool-" + tool.toString().toLowerCase()));
        }
        toolNone = atlasGame.createSprite("tool-none");
        lavaball = atlasGame.createSprite("lavaball");

        pickaxe = Gdx.audio.newSound(Gdx.files.internal("pickaxe.wav"));
        die = Gdx.audio.newSound(Gdx.files.internal("die.wav"));
        pickupLoot = Gdx.audio.newSound(Gdx.files.internal("pickupLoot.wav"));
    }

    private static Sprite colorSprite(Color color, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        return new Sprite(tex);
    }
}
