package de.doccrazy.ld29.game;

import box2dLight.DirectionalLight;
import box2dLight.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.doccrazy.ld29.core.Debug;
import de.doccrazy.ld29.core.Resource;
import de.doccrazy.ld29.game.base.ActorListener;
import de.doccrazy.ld29.game.base.Box2dActor;
import de.doccrazy.ld29.game.level.Category;
import de.doccrazy.ld29.game.world.GameRules;
import de.doccrazy.ld29.game.world.GameWorld;

public class GameRenderer implements ActorListener {
    // here we set up the actual viewport size of the game in meters.
    public static float UNIT_WIDTH = GameRules.LEVEL_WIDTH;
    public static float UNIT_HEIGHT = UNIT_WIDTH*9f/16f;
	private static final float CAM_PPS = 5f;

    private SpriteBatch batch = new SpriteBatch();
    private Scaling bgScaling = Scaling.fill;
    private GameWorld world;
    private OrthographicCamera camera;
    private Box2DDebugRenderer renderer;
	private float zoom = 1;
	private float zoomDelta = 0;
	private float camY;
    private boolean animateCamera;

    public GameRenderer(GameWorld world) {
        this.world = world;
        // set the game stage viewport to the meters size
        world.stage.setViewport(new ExtendViewport(UNIT_WIDTH, UNIT_HEIGHT));
        renderer = new Box2DDebugRenderer();

        // we obtain a reference to the game stage camera. The camera is scaled to box2d meter units
        camera = (OrthographicCamera) world.stage.getCamera();

        world.rayHandler.setAmbientLight(new Color(0.05f, 0.1f, 0.05f, 0.15f));
        DirectionalLight sun = new DirectionalLight(world.rayHandler, 1000, new Color(1.0f, 1.0f, 0.5f, 0.6f), -60);
        Light.setContactFilter(Category.DEFAULT, (short)0, Category.LEVEL);
        camY = UNIT_HEIGHT/2;
    }

    private void drawBackground() {
        batch.setProjectionMatrix(world.stage.getCamera().combined);
        batch.begin();
        Vector2 bgSize = bgScaling.apply(UNIT_WIDTH, UNIT_HEIGHT, world.stage.getWidth(), world.stage.getHeight());
        batch.draw(Resource.backgroundHigh, world.stage.getWidth()/2 - bgSize.x/2, 0, bgSize.x, bgSize.y);
        batch.draw(Resource.backgroundLow, world.stage.getWidth()/2 - bgSize.x/2, -bgSize.y, bgSize.x, bgSize.y);
        batch.end();
    }

    public void render() {
        positionCamera();

        camera.update();
        drawBackground();
        // game stage rendering
        world.stage.draw();

        // box2d debug renderering (optional)
        if (Debug.ON) {
            renderer.render(world.box2dWorld, camera.combined);
        }

        world.rayHandler.setCombinedMatrix(camera.combined);
        world.rayHandler.updateAndRender();
    }

	private void positionCamera() {
	    zoom = MathUtils.clamp(zoom + zoomDelta*0.02f, 1f, 2f);
		ExtendViewport vp = (ExtendViewport)world.stage.getViewport();
		vp.setMinWorldWidth(UNIT_WIDTH*zoom);
		vp.setMinWorldHeight(UNIT_HEIGHT*zoom);
        world.stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        if (animateCamera) {
            camY -= Gdx.graphics.getDeltaTime() * CAM_PPS;
        }
        camera.position.x = world.stage.getWidth() / 2;
        camera.position.y = Math.max(camY, UNIT_HEIGHT/2 - GameRules.LEVEL_HEIGHT + 1);
	}

	@Override
	public void actorAdded(Box2dActor actor) {
	}

	@Override
	public void actorRemoved(Box2dActor actor) {
	}

	public void setZoomDelta(float zoomDelta) {
		this.zoomDelta = zoomDelta;
	}

	public void animateCamera() {
	    animateCamera = true;
	}

}