package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class UndaTail extends Enemy {
    public UndaTail(float x, float y, Player player) {
        super(x, y, 30, 20, 5, player);

        aniWalks = TextureRegion.split(new Texture(Gdx.files.internal("enemies/undaTail/UndaTailWalk.png")), 30, 22)[0];
        for (TextureRegion region : aniWalks) {
            region.flip(true, false);
        }
        currentFrame = aniWalks[0];

        maxSpeed = 0.5F;
        velX = maxSpeed;
        jumpForce = 8;
        gravity = -0.2F;
        isHeavy = true;

        nextXBounds = bounds;
        nextYBounds = bounds;
    }

    public UndaTail(float x, float y, float speedX, float speedY, Player player) {
        super(x, y, 30, 20, 5, player);

        aniWalks = TextureRegion.split(new Texture(Gdx.files.internal("enemies/undaTail/UndaTailWalk.png")), 30, 22)[0];
        for (TextureRegion region : aniWalks) {
            region.flip(true, false);
        }
        currentFrame = aniWalks[0];

        maxSpeed = 0.5F;
        velX = speedX;
        velY = speedY;
        jumpForce = 8;
        gravity = -0.2F;
        isHeavy = true;

        nextXBounds = bounds;
        nextYBounds = bounds;
    }
}
