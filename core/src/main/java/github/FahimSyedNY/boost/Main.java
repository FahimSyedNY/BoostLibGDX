package github.FahimSyedNY.boost;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import entities.Background;
import entities.map.Level;
import entities.Player;
import inputs.Inputs;

///** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private static boolean dynaCam;

    Player player;
    Level level;
    Background[] BGlayers;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        camera = new OrthographicCamera();
        viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply();

        player = new Player(Level.getTilemapData().tileSize * 2, Level.getTilemapData().tileSize * 4);
        level = new Level(player);
        shapeRenderer = new ShapeRenderer();

        BGlayers = new Background[6];
        for (int i = 0; i < 6; i++) {
            BGlayers[i] = new Background(((double) i / 10) + 0.1, i + 1);
        }

        Inputs inputProcessor = new Inputs(player);
        Gdx.input.setInputProcessor(inputProcessor);
    }

    @Override
    public void render() {
        player.update();

        if (dynaCam) camera.position.set(Player.xDelta + (viewport.getWorldWidth() / 2f) - 32, Player.yDelta + (viewport.getWorldHeight() / 2f) - 33 + player.totalCamShift, 0);
        else camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();

        for (Background bg : BGlayers) {
            bg.render(batch, camera);
        }
        level.render(batch);
        player.render(batch); // Cast Y to int for rendering

        batch.end();

        if (!dynaCam) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            level.renderDebug(shapeRenderer);
            player.renderDebug(shapeRenderer);
            shapeRenderer.end();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }

    public static void isDynaCam() {
        dynaCam = !dynaCam;
    }
}
