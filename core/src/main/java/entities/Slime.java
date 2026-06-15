package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Slime extends Enemy {
    public Slime(float x, float y, Player player) {
        super(x, y, 14, 10, 1, player);

        aniWalks = TextureRegion.split(new Texture(Gdx.files.internal("enemies/slime/SlimeWalk.png")), 14, 15)[0];
        for (TextureRegion region : aniWalks) {
            region.flip(true, false);
        }
        currentFrame = aniWalks[0];

        maxSpeed = 1.2F;
        velX = maxSpeed;
        jumpForce = 5;
        gravity = -0.6F;

        nextXBounds = bounds;
        nextYBounds = bounds;
    }

    public Slime(float x, float y, float speedX, float speedY, Player player) {
        super(x, y, 14, 10, 1, player);

        aniWalks = TextureRegion.split(new Texture(Gdx.files.internal("enemies/slime/SlimeWalk.png")), 14, 15)[0];
        for (TextureRegion region : aniWalks) {
            region.flip(true, false);
        }
        currentFrame = aniWalks[0];

        maxSpeed = 1.2F;
        velX = speedX;
        velY = speedY;
        jumpForce = 5;
        gravity = -0.6F;

        nextXBounds = bounds;
        nextYBounds = bounds;
    }
}
