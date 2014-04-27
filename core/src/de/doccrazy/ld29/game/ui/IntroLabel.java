package de.doccrazy.ld29.game.ui;

import net.dermetfan.utils.libgdx.Typewriter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld29.core.Resource;

public class IntroLabel extends Label {
    private Typewriter typewriter = new Typewriter();
    private static final String[] TEXT = {
        "Countless hours you sat wondering,\nafter digging your way down\n straight into a pit of lava,\n\nwhat evil mind could have devised \nthe position of the block \nthat became your doom?",
        "Well, wonder no more, \nfor the time of payback has come",
        "Let the Hate flow through you!"
    };
    private static final float[] TIMING = {12f, 4.5f, 4.5f};
    private int line = 0;
    private Label titleLabel;
    private UiRoot root;

    public IntroLabel(UiRoot root) {
        super("", new LabelStyle(Resource.fontSmall, new Color(1f, 0.4f, 0.3f, 0.7f)));
        this.root = root;
        titleLabel = new TitleLabel();
        typewriter.setCharsPerSecond(20);
        typewriter.getInterpolator().setInterpolation(Interpolation.linear);
        typewriter.getAppender().set(new CharSequence[] {"", ".", "..", "..."}, 1.5f / 4f);

        setAlignment(Align.center);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            stage.addActor(titleLabel);
        }
        root.getWorld().startSpawn();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        setPosition(0, getStage().getHeight() / 2);
        setWidth(getStage().getWidth());
        CharSequence txt;
        txt = typewriter.updateAndType(TEXT[line], delta);
        if (line == 0 && typewriter.getTime() > TIMING[line]) {
            setLine(1);
        } else if (line == 1 && typewriter.getTime() > TIMING[line]) {
            setLine(2);
            root.getRenderer().animateCamera();
            typewriter.getAppender().setAppendices(new CharSequence[] {"", "", "", ""});
        } else if (line == 2 && typewriter.getTime() > TIMING[line]) {
            remove();  // root.getRenderer().animateCamera();  //TODO
            titleLabel.remove();
            root.showToolbar();
            root.getInput().setEnabled(true);
            root.getWorld().startGame();
        }
        setText(txt);
    }

    private void setLine(int line) {
        this.line = line;
        typewriter.setTime(0);
    }
}
