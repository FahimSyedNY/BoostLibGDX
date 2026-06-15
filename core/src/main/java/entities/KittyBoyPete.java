package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import github.FahimSyedNY.boost.Main;

public class KittyBoyPete extends Enemy {
    public KittyBoyPete(float x, float y, Player player) {
        super(x, y, 28, 64, 15, player);

        currentFrame = new TextureRegion(new Texture(Gdx.files.internal("enemies/kittyBoyPete/KittyBoyPete.png")));
        currentFrame.flip(true, false);

        maxSpeed = 0.3F;
        velX = maxSpeed;
        jumpForce = 6;
        gravity = -0.1F;
        isHeavy = true;
        range = 384;

        nextXBounds = bounds;
        nextYBounds = bounds;
    }

    @Override
    public void update() {
        if (!isDead) {
            velUpdate();
            inRange = player.getXPlayer() - range < xDelta && xDelta < player.getXPlayer() + range
                && player.getYPlayer() - range < yDelta && yDelta < player.getYPlayer() + range;
            if (!isInvincible) playerHit();
            if (!isInvincible) hitPlayer();
            move();
            if (isInvincible) invDuration();
            addEnemy();
        }
    }

    public void addEnemy() {
        if (inRange && !isInvincible && (int) (Math.random() * 150) == 0) {
            int value = (int) (Math.random() * 3);

            float pushDirection = (velX != 0) ? Math.signum(velX) : 1f;
            float speedX = 10f * pushDirection;

            if (value == 0) Main.addEnemy(new Slemis(xDelta, yDelta, speedX, 10, player));
            else Main.addEnemy(new Slime(xDelta, yDelta, speedX, 10, player));
        }
    }

}
