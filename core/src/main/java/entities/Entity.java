package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class Entity {
    protected float xDelta, yDelta, velX, velY, jumpForce, maxSpeed, gravity, floorY = 0;
    protected boolean isGrounded = false;
    protected boolean isInvincible = false;

    protected int health;
    protected int maxHealth;
    protected Color oldColor;
    protected Rectangle bounds, nextXBounds, nextYBounds, feet;
    protected float flip = 1.0f;
    protected TextureRegion currentFrame;
    protected TextureRegion[] hearts;
    protected int aniTick;
    protected int invTick;
    protected float feetLength;

    public Entity(float x, float y, float width, float height,  int health) {
        this.xDelta = x;
        this.yDelta = y;

        bounds = new Rectangle(x, y, width, height);
        nextXBounds = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
        nextYBounds = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);

        this.health = health;
        this.maxHealth = health;
        hearts = TextureRegion.split(new Texture(Gdx.files.internal("other/Hearts.png")), 9, 9)[0];
    }

    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.rect(feet.x, feet.y, feet.width, feet.height);
    }

    public void renderHearts(SpriteBatch batch) {
        int heartFull;
        for (int i = 0; i < maxHealth; i++) {
            if (i < health) heartFull = 1;
            else heartFull = 0;
            batch.draw(hearts[heartFull], bounds.x + i * 9 - (maxHealth * 9 - bounds.getWidth()) / 2, bounds.y + bounds.height);
        }
    }

    public void heal() {
        if (health < maxHealth) {
            health++;
        }
    }

    public void invDuration() {
        if (invTick < 60) invTick++;
        if (invTick == 60) isInvincible = false;
    }

    public void setInvincible() {
        isInvincible = true;
        invTick = 0;
    }

    public boolean getInvincible() {
        return isInvincible;
    }

    //<editor-fold desc="Getters/Setters">
    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getNextXBounds() {
        return nextXBounds;
    }

    public Rectangle getNextYBounds() {
        return nextYBounds;
    }

    public Rectangle getFeet() {
        return feet;
    }

    public double getVelX() {
        return velX;
    }

    public double getVelY() {
        return velY;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setHealth(int value) {
        health = value;
    }

    public void setVelX(float val) {
        velX = val;
    }

    public void setVelY(float val) {
        velY = val;
    }

    public void setIsGrounded(boolean grounded) {
        isGrounded = grounded;
    }

    public void setXPosition(float totalX) {
        xDelta = totalX;
        bounds.x = totalX;
    }

    public void setYPosition(float totalY) {
        yDelta = totalY;
        bounds.y = totalY;
    }
    //</editor-fold>
}
