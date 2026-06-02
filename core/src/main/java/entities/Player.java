package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    private boolean rightPressed = false;
    private boolean leftPressed = false;
    private boolean jumpPressed = false;// Track jump key state
    private boolean shiftPressed = false;
    private boolean rightFaced = false;

    // Horizontal Physics
    private double velX = 0;
    private final double ACCELERATION = 0.8;
    private final double FRICTION = 0.85;
    private double MAX_SPEED = 5.0;

    // Vertical Physics constants
    private double velY = 0;
    private final double GRAVITY = -0.6;
    private final double JUMP_FORCE = 13.0;
    private final int FLOOR_Y = 32;
    private boolean isGrounded = true;

    public static float xDelta;
    public static float yDelta;

    private static int aniTick;
    private static int aniIndex;

    private Texture aniIdleSet;
    private TextureRegion[] aniIdles;

    private TextureRegion currentPlayer;




    public Player(int x, int y) {
        xDelta = x;
        yDelta = y;

        aniIdleSet = new Texture(Gdx.files.internal("player/Punk_idle.png"));
        aniIdles = TextureRegion.split(aniIdleSet, 48, 48)[0];
        currentPlayer = aniIdles[0];
    }

    public void render(SpriteBatch batch) {
        batch.draw(currentPlayer, xDelta,  yDelta); // Cast Y to int for rendering
    }

    public void update() {
        if (leftPressed) velX -= ACCELERATION;
        if (rightPressed) velX += ACCELERATION;

        if (!leftPressed && !rightPressed) {
            velX *= FRICTION;
            if (Math.abs(velX) < 0.1) velX = 0;
        }

        if (velX > MAX_SPEED) velX = MAX_SPEED;
        if (velX < -MAX_SPEED) velX = -MAX_SPEED;

        // --- VERTICAL MOVEMENT (JUMPING & GRAVITY) ---
        if (jumpPressed && isGrounded) {
            velY = JUMP_FORCE; // Apply instant upward thrust
            isGrounded = false; // Mario leaves the ground
        }

        // Apply constant gravity pulling down if in mid-air
        if (!isGrounded) velY += GRAVITY;

        // --- GROUND COLLISION ---
        if (yDelta + velY <= FLOOR_Y) {
            yDelta = FLOOR_Y; // Snap to floor so he doesn't sink
            velY = 0;         // Stop falling downward
            isGrounded = true; // He is back on solid ground
        }

        // --- POSITION UPDATES ---
        xDelta += velX;
        yDelta += velY; // Move vertically

        aniTick++;
        if (aniTick > 59) aniTick = 0;
        currentPlayer = aniIdles[aniTick / (60 / aniIdles.length)];
    }


    // Inputs
    public void Jump(boolean isTrue) {
        jumpPressed = isTrue;
    }

    public void Right(boolean isTrue) {
        rightPressed = isTrue;
    }

    public void Left(boolean isTrue) {
        leftPressed = isTrue;
    }

    public void Sprint(boolean isTrue) {
        shiftPressed = isTrue;
        if (isTrue) MAX_SPEED = 15;
        else MAX_SPEED = 5;
    }
}
