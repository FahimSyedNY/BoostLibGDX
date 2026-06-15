package entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class Collectable {
    protected float xDelta, yDelta, timer, value;
    protected Rectangle bounds;
    protected Player player;
    protected boolean collected;
    protected TextureRegion currentFrame;
    protected Sound sound;

    public Collectable(float x, float y, float width, float height, Player player) {
        this.player = player;
        bounds = new Rectangle(x, y, width, height);
        xDelta = x;
        yDelta = y;
    }

    public void render(SpriteBatch batch) {
        if (!collected) batch.draw(currentFrame, xDelta, yDelta + value);
    }

    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void lerpY() {
        timer += 0.05f;
        value = (float)((Math.sin(timer) + 1));
    }

    public void update() {
    }
}
