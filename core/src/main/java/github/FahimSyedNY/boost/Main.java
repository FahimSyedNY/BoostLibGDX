package github.FahimSyedNY.boost;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import entities.*;
import map.Level;
import inputs.Inputs;
import map.Background;
import map.TilemapData;

import java.util.ArrayList;

///** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    //<editor-fold desc="Class Vars">
    private static SpriteBatch batch;
    private static BitmapFont font;
    private static OrthographicCamera camera;
    private static Viewport viewport;
    private static ShapeRenderer shapeRenderer;
    private static boolean showHitboxes, paused, musicMuted;

    // Added separate camera and viewport exclusively for UI rendering
    private static OrthographicCamera uiCamera;
    private static Viewport uiViewport;
    GlyphLayout pauseScreen;
    private static boolean cameraZoomUp, cameraZoomDown;
    private static float shakeIntensity = 0f, shakeDuration = 0f;

    private static Player player;
    private static Level level;
    private static ArrayList<Enemy> enemies, enemiesToSpawn;
    private static ArrayList<Collectable> collectables = new ArrayList<>();
    private static Background[] BGlayers;
    private static Music bgMusic;
    //</editor-fold>


    //Any Audio made by Fahim
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply();

        uiCamera = new OrthographicCamera();
        uiViewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), uiCamera);
        uiViewport.apply();
        pauseScreen = new GlyphLayout(font, "Press Esc To Continue");

        player = new Player(Level.getTilemapData().tileSize * 3, Level.getTilemapData().tileSize * 43, 12f, 28f);
        level = new Level();
        shapeRenderer = new ShapeRenderer();
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/BG Music.mp3"));
        bgMusic.setLooping(true);
        bgMusic.play();

        enemies = new ArrayList<>();
        enemiesToSpawn = new ArrayList<>();

        int tileSize = Level.getTilemapData().tileSize;
        int mapHeight = Level.getTilemapData().mapHeight;
        for (TilemapData.Tile enemy: Level.getEnemyData().tiles) {
            if (enemy.attributes.enemyType.equals("slime")) {
                enemies.add(new Slime(enemy.x * tileSize, (mapHeight - enemy.y - 1) * tileSize, player));
            }
            if (enemy.attributes.enemyType.equals("slemis")) {
                enemies.add(new Slemis(enemy.x * tileSize, (mapHeight - enemy.y - 1) * tileSize, player));
            }
            if (enemy.attributes.enemyType.equals("undaTail")) {
                enemies.add(new UndaTail(enemy.x * tileSize, (mapHeight - enemy.y - 1) * tileSize, player));
            }
            if (enemy.attributes.enemyType.equals("kittyBoyPete")) {
                enemies.add(new KittyBoyPete(enemy.x * tileSize, (mapHeight - enemy.y - 1) * tileSize, player));
            }
            if (enemy.attributes.enemyType.equals("coin")) {
                collectables.add(new Coin(enemy.x * tileSize + (float) tileSize / 2 - 3, (mapHeight - enemy.y - 1) * tileSize + 3, player));
            }
        }

        BGlayers = new Background[6];
        for (int i = 0; i < 6; i++) {
            BGlayers[i] = new Background(((double) i / 10) + 0.1, i + 1);
        }

        Inputs inputProcessor = new Inputs(player);
        Gdx.input.setInputProcessor(inputProcessor);
    }

    @Override
    public void render() {
        if (!paused) {
            player.update();

            //Camera update by Ayden
            //<editor-fold desc="Update Camera">
            float targetX = player.getXDelta() + (viewport.getWorldWidth() / 2f) - 32;
            float targetY = player.getYDelta() + (viewport.getWorldHeight() / 2f) - 33 + player.getTotalCamShift();

            if (shakeDuration > 0) {
                targetX += MathUtils.random(-shakeIntensity, shakeIntensity);
                targetY += MathUtils.random(-shakeIntensity, shakeIntensity);

                shakeDuration -= Gdx.graphics.getDeltaTime();
            }

            if (cameraZoomUp) camera.zoom += 0.02f;
            if (cameraZoomDown) camera.zoom -= 0.02f;

            camera.position.set(targetX, targetY, 0);
            camera.update();

            ScreenUtils.clear(0.008f, 0.082f, 0.11f, 1f);


            viewport.apply();
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            //</editor-fold>


            //Background by Fahim
            for (Background bg : BGlayers) bg.render(batch, camera);
            level.render(batch);
            for (Collectable collectable : collectables) {
                collectable.update();
                collectable.render(batch);
            }
            for (Enemy enemy : enemies) {
                enemy.update();
                enemy.render(batch);
            }

            player.render(batch);

            batch.end();

            //Debug included by Ayden
            if (showHitboxes) {
                shapeRenderer.setProjectionMatrix(camera.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                for (Collectable collectable : collectables) {
                    collectable.renderDebug(shapeRenderer);
                }
                for (Enemy enemy : enemies) {
                    enemy.renderDebug(shapeRenderer);
                }
                level.renderDebug(shapeRenderer);
                player.renderDebug(shapeRenderer);
                shapeRenderer.end();
            }

            enemies.addAll(enemiesToSpawn);
            enemiesToSpawn.clear();

            //UI
            uiViewport.apply();
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();

            float uiX = 20;
            float uiY = uiViewport.getWorldHeight() - 20;
            font.draw(batch, "Coins: " + player.getCoins(), uiX, uiY);

            batch.end();
        } else {
            // Pause menu done by Evan
            ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
            uiViewport.apply();
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();


            float uiX = (uiViewport.getWorldWidth() / 2) - (pauseScreen.width / 2);
            float uiY = (uiViewport.getWorldHeight() / 2) + (pauseScreen.height / 2);
            font.draw(batch, pauseScreen, uiX, uiY);

            batch.end();
        }
        System.out.println(paused);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }

    public static void addCollectable(Collectable collectable) {
        collectables.add(collectable);
    }

    public static void addEnemy(Enemy enemy) {
        enemiesToSpawn.add(enemy);
    }

    public static void setPause(boolean shouldPause) {
        paused = shouldPause;

        if (paused) {
            bgMusic.pause();
        } else {
            bgMusic.play();
            if (player.getHealth() == 0) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    public static void setPause() {
        paused = !paused;

        if (paused) {
            bgMusic.pause();
        } else {
            bgMusic.play();
            if (player.getHealth() == 0) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    public static void muteMusic() {
        musicMuted = !musicMuted;
        if (musicMuted) bgMusic.setVolume(0f);
        else bgMusic.setVolume(0.5f);
    }

    public static void cameraZoomUp(boolean isTrue) {
        cameraZoomUp = isTrue;
    }

    public static void cameraZoomDown(boolean isTrue) {
        cameraZoomDown = isTrue;
    }

    public static void shakeCamera(float intensity, float durationInSeconds) {
        shakeIntensity = intensity;
        shakeDuration = durationInSeconds;
    }

    public static void isDynaCam() {
        showHitboxes = !showHitboxes;
    }
}
