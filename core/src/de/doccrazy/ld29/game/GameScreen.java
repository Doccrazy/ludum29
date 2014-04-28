package de.doccrazy.ld29.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.doccrazy.ld29.game.ui.UiRoot;
import de.doccrazy.ld29.game.world.GameWorld;

public class GameScreen implements Screen {
    public static int SCREEN_WIDTH = 1280;
    public static int SCREEN_HEIGHT = (int)(SCREEN_WIDTH*9f/16f);

    private GameWorld world; // contains the game world's bodies and actors.
    private GameRenderer renderer; // our custom game renderer.
    private Stage uiStage; // stage that holds the GUI. Pixel-exact size.
    private SpriteBatch batch;
    private Scaling bgScaling = Scaling.fill;

	@Override
	public void show() {
		batch = new SpriteBatch();
		uiStage = new Stage(new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT));

        world = new GameWorld();
        renderer = new GameRenderer(world);

		Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, world.stage));

		new UiRoot(uiStage, world, renderer);
	}

	@Override
	public void render(float delta) {
		update(delta);

		Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);

        renderer.render(); // draw the box2d world
        uiStage.draw(); // draw the GUI
        Table.drawDebug(uiStage);
	}

	private void update(float delta) {
        world.update(delta); // update the box2d world
        uiStage.act(delta); // update GUI
	}

	@Override
	public void resize(int width, int height) {
		uiStage.getViewport().update(width, height, true);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		uiStage.dispose();
		batch.dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
