package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;

public class Background {
    private final Texture background;
    private final double parallax;
    private final int width;

    public Background(Double parallax, int num) {
        background = new Texture(Gdx.files.internal("bg/" + num + ".png"));
        this.parallax = parallax;
        width = background.getWidth();
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        float cameraLeft = camera.position.x - (camera.viewportWidth / 2f);
        float cameraBottom = camera.position.y - (camera.viewportHeight / 2f);

        float pos1 = cameraLeft + (float) -((camera.position.x * parallax) % width);
        float yPos = cameraBottom + (float) -(camera.position.y * parallax);

        batch.draw(background, pos1, yPos);
        batch.draw(background, pos1 + width, yPos);
    }
}
