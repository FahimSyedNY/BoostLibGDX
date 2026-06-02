package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    private boolean rightPressed = false;
    private boolean leftPressed = false;
    private boolean jumpPressed = false;// Track jump key state
    private boolean shiftPressed = false;
    private boolean downPressed = false;
    private boolean upPressed = false;

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
    public static float xBound;
    public static float yBound;

    private static int aniTick;
    private static int aniIndex;

    private Texture aniIdleSet;
    private Texture aniRunSet;
    private TextureRegion[] aniIdles;
    private TextureRegion[] aniRuns;

    private TextureRegion currentPlayer;
    private float flip = 1.0f;

    private OrthographicCamera camera;
    private boolean shiftCamera = false;
    private float cameraJumpShift = 0f;
    private float cameraDownShift = 0f;
    private float cameraUpShift = 0f;
    public float totalCamShift = 0f;



    public Player(int x, int y, OrthographicCamera camera) {
        xDelta = x;
        yDelta = y;
        this.camera = camera;

        aniIdleSet = new Texture(Gdx.files.internal("player/Punk_idle.png"));
        aniRunSet = new Texture(Gdx.files.internal("player/Punk_run.png"));
        aniIdles = TextureRegion.split(aniIdleSet, 48, 48)[0];
        aniRuns = TextureRegion.split(aniRunSet, 48, 48)[0];
        currentPlayer = aniIdles[0];
    }

    public void render(SpriteBatch batch) {
        batch.draw(
            currentPlayer,
            xDelta + xBound,
            yDelta + yBound,
            currentPlayer.getRegionWidth() / 2f,
            currentPlayer.getRegionHeight() / 2f,
            currentPlayer.getRegionWidth(),
            currentPlayer.getRegionHeight(),
            flip, 1.0f,
            0.0f
        );
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
            shiftCamera = true;
            velY = JUMP_FORCE; // Apply instant upward thrust
            isGrounded = false; // Mario leaves the ground
        }

        // Apply constant gravity pulling down if in mid-air
        if (!isGrounded) {
            velY += GRAVITY;
            if (cameraJumpShift < 20) {
                cameraJumpShift++;
                cameraJumpShift *= 1.1;
            }
        }

        if (isGrounded) {
            if (cameraJumpShift != 0)  cameraJumpShift /= 1.2;
            if (downPressed && cameraDownShift > -80 && !upPressed) {
                cameraDownShift--;
                cameraDownShift *= 1.1;
            }
            if (upPressed && cameraUpShift < 80 && !downPressed) {
                cameraUpShift++;
                cameraUpShift *= 1.1;
            }
        }

        if ((!downPressed) || (!upPressed) || (downPressed && upPressed)) {
            if (cameraDownShift != 0) {
                cameraDownShift /= 1.2;
                if (cameraDownShift > -0.1) cameraDownShift = 0;
            }
            if (cameraUpShift != 0) {
                cameraUpShift /= 1.2;
                if (cameraUpShift < 0.1) cameraUpShift = 0;
            }
        }



        // --- GROUND COLLISION ---
        if (yDelta + yBound + velY <= FLOOR_Y) {
            yDelta = FLOOR_Y;// Snap to floor so he doesn't sink
            yBound = 0;
            velY = 0;         // Stop falling downward
            isGrounded = true; // He is back on solid ground
            shiftCamera = false;
        }

        // --- POSITION UPDATES ---
        if (velX > 0 && xBound < 100) xBound += velX;
        else if (velX < 0 && xBound > 0) xBound += velX;
        else xDelta += velX;

        if (velY > 0 && yBound < 120) yBound += velY * 0.8;
        else if (velY < 0 && yBound > 0) yBound += velY;
        else if (velY > 0) yDelta += velY * 0.8;
        else velY += velY;

        if (velX > 0) flip = 1.0f;
        if (velX < 0) flip = -1.0f;

        aniTick++;
        if (aniTick > 59) aniTick = 0;
        if (Math.abs(velX) > 1) currentPlayer = aniRuns[aniTick / (60 / aniRuns.length)];
        else currentPlayer = aniIdles[aniTick / (60 / aniIdles.length)];

        totalCamShift = cameraJumpShift + cameraDownShift * 5 + cameraUpShift * 2;
    }


    // Inputs
    public void Jump(boolean isTrue) {
        jumpPressed = isTrue;
        if (!isTrue) velY = velY * 0.5;
    }

    public void Right(boolean isTrue) {
        rightPressed = isTrue;
    }

    public void Left(boolean isTrue) {
        leftPressed = isTrue;
    }

    public void Sprint(boolean isTrue) {
        shiftPressed = isTrue;
        if (isTrue) MAX_SPEED = 10;
        else MAX_SPEED = 5;
    }

    public void Down(boolean isTrue) {
        downPressed = isTrue;
    }

    public void Up(boolean isTrue) {
        upPressed = isTrue;
    }
}
