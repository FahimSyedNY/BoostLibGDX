package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Slemis extends Enemy {
    public Slemis(float x, float y, Player player) {
        super(x, y, 15, 15, 3, player);

        aniWalks = TextureRegion.split(new Texture(Gdx.files.internal("enemies/slemis/SlemisWalk.png")), 15, 20)[0];
        for (TextureRegion region : aniWalks) {
            region.flip(true, false);
        }
        currentFrame = aniWalks[0];

        maxSpeed = 0.7F;
        velX = maxSpeed;
        jumpForce = 8;
        gravity = -0.4F;

        nextXBounds = bounds;
        nextYBounds = bounds;
    }

    public Slemis(float x, float y, float speedX, float speedY, Player player) {
        super(x, y, 15, 15, 3, player);

        aniWalks = TextureRegion.split(new Texture(Gdx.files.internal("enemies/slemis/SlemisWalk.png")), 15, 20)[0];
        for (TextureRegion region : aniWalks) {
            region.flip(true, false);
        }
        currentFrame = aniWalks[0];

        maxSpeed = 0.7F;
        velX = speedX;
        velY = speedY;
        jumpForce = 8;
        gravity = -0.4F;

        nextXBounds = bounds;
        nextYBounds = bounds;
    }
}
