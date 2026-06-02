package github.FahimSyedNY.boost;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import entities.Background;
import entities.Level;
import entities.Player;
import inputs.KeyboardInputs;

///** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private OrthographicCamera camera;
    private Viewport viewport;

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

        player = new Player(0, 0);
        level = new Level();

        BGlayers = new Background[6];
        for (int i = 0; i < 6; i++) {
            BGlayers[i] = new Background(((double) i / 10) + 0.1, i + 1);
        }

        KeyboardInputs inputProcessor = new KeyboardInputs(player);
        Gdx.input.setInputProcessor(inputProcessor);
    }

    @Override
    public void render() {
        player.update();


        camera.position.set(Player.xDelta + (viewport.getWorldWidth() / 2f) - 32, Player.yDelta + (viewport.getWorldHeight() / 2f) - 32, 0);
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
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
