package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import entities.map.Level;

public class Player {
    //<editor-fold desc="Class Vars">
    private boolean rightPressed = false;
    private boolean leftPressed = false;
    private boolean jumpPressed = false;
    private boolean shiftPressed = false;
    private boolean downPressed = false;
    private boolean upPressed = false;

    // Horizontal Physics
    private double velX = 0;
    private final double ACCELERATION = 0.6;
    private final double FRICTION = 0.85;
    private double MAX_SPEED = 3.0;

    // Vertical Physics constants
    private double velY = 0;
    private final double GRAVITY = -0.6;
    private final double JUMP_FORCE = 13.0;
    private final int FLOOR_Y = 0;
    private boolean isGrounded = true;

    public static Rectangle bounds;
    public static Rectangle nextXBounds;
    public static Rectangle nextYBounds;
    public static Rectangle feet;

    public static float xDelta;
    public static float yDelta;
    private static float xBound;
    private static float yBound;

    private static int aniTick;

    private final TextureRegion[] aniIdles;
    private final TextureRegion[] aniWalks;
    private final TextureRegion[] aniRuns;

    private TextureRegion currentPlayer;
    private float flip = 1.0f;

    private float cameraJumpShift = 0f;
    private float cameraDownShift = 0f;
    private float cameraUpShift = 0f;
    public float totalCamShift = 0f;
    //</editor-fold>

    public Player(int x, int y) {
        xDelta = x;
        yDelta = y;

        Texture aniIdleSet = new Texture(Gdx.files.internal("player/LittleGuyIdle.png"));
        Texture aniWalkSet = new Texture(Gdx.files.internal("player/Punk_run.png"));
        Texture aniRunSet = new Texture(Gdx.files.internal("player/LittleGuyRun.png"));
        aniIdles = TextureRegion.split(aniIdleSet, 28, 32)[0];
        aniWalks = TextureRegion.split(aniWalkSet, 48, 48)[0];
        aniRuns = TextureRegion.split(aniRunSet, 28, 32)[0];
        for (TextureRegion region : aniRuns) {
            region.flip(true, false);
        }

        bounds = new Rectangle(x, y, 28F, 32F);
        nextXBounds = new Rectangle(x, y, 28F, 32F);
        nextYBounds = new Rectangle(x, y, 28F, 32F);
        feet = new Rectangle(bounds.x + 2, bounds.y - 2, 24, 2);
    }

    public void render(SpriteBatch batch) {
        batch.draw(
            currentPlayer,
            xDelta + xBound - 8,
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
        velUpdate();

        cameraY();

        if (!Level.wouldXCollide()) {
            moveX();
            Level.checkHorizontalCollisions();
        } else {
            velX = 0;
        }

        isGrounded = Level.isStandingOnGround();

        if (!Level.wouldYCollide()) {
            moveY();
            Level.checkVerticalCollisions();
        } else {
            velY = 0;
        }


        animate();
    }

    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.rect(feet.x, feet.y, feet.width, feet.height);
    }


    //<editor-fold desc="Game Loop">
    public void velUpdate() {
        //Horizontal Movement
        if (leftPressed) velX -= ACCELERATION;
        if (rightPressed) velX += ACCELERATION;

        if (!leftPressed && !rightPressed) {
            velX *= FRICTION;
            if (Math.abs(velX) < 0.1) velX = 0;
        }

        if (velX > MAX_SPEED) velX = MAX_SPEED;
        if (velX < -MAX_SPEED) velX = -MAX_SPEED;

        // Vertical Movement
        if (!isGrounded) {
            velY += GRAVITY;
        } else if (velY < 0) {
            velY = 0;
        }

        if (jumpPressed && isGrounded) {
            velY = JUMP_FORCE;
            isGrounded = false;
        }

        nextXBounds.setPosition((float) (bounds.x + velX), bounds.y);
        nextYBounds.setPosition(bounds.x, (float) (bounds.y + velY));
    }

    public void cameraY() {
        if (!isGrounded && velY > 0) {
            if (cameraJumpShift < 20) {
                cameraJumpShift++;
                cameraJumpShift *= 1.05F;
            }
        }

        if (isGrounded) {
            if (cameraJumpShift != 0)  cameraJumpShift /= 1.2F;

            if (downPressed && cameraDownShift > -30 && !upPressed) {
                cameraDownShift--;
                cameraDownShift *= 1.1F;
            }
            if (upPressed && cameraUpShift < 30 && !downPressed) {
                cameraUpShift++;
                cameraUpShift *= 1.1F;
            }
        }

        if (cameraDownShift != 0) {
            cameraDownShift /= 1.2F;
            if (cameraDownShift > -0.1) cameraDownShift = 0;
        }
        if (cameraUpShift != 0) {
            cameraUpShift /= 1.2F;
            if (cameraUpShift < 0.1) cameraUpShift = 0;
        }

        totalCamShift = cameraJumpShift + cameraDownShift * 5 + cameraUpShift * 2;
    }

    private void moveX() {
        if (velX > 0 && xBound < 100) xBound += (float) velX;
        else if (velX < 0 && xBound > 0) xBound += (float) velX;
        else xDelta += (float) velX;

        if (velX > 0) flip = 1.0f;
        if (velX < 0) flip = -1.0f;

        // Update bounds immediately for X checking
        bounds.setPosition(xDelta + xBound, yDelta + yBound);
        feet.setPosition(bounds.x + 2, bounds.y - 2);
    }

    private void moveY() {
        if (velY > 0 && yBound < 120) yBound += (float) (velY * 0.8);
        else if (velY < 0 && yBound > 0) yBound += (float) velY;
        else if (velY > 0) yDelta += (float) (velY * 0.8);
        else yDelta += (float) velY;

        if ((yDelta + yBound) <= FLOOR_Y) {
            yDelta = FLOOR_Y;
            yBound = 0;
            velY = 0;
            isGrounded = true;
        }

        bounds.setPosition(xDelta + xBound, yDelta + yBound);
        feet.setPosition(bounds.x + 2, bounds.y - 2);
    }

    public void animate() {
        aniTick++;
        if (aniTick > 59 && !shiftPressed) aniTick = 0;
        else if (aniTick > 59 && shiftPressed) aniTick = 18;

        if (Math.abs(velX) > 1 && !shiftPressed) currentPlayer = aniWalks[aniTick / (60 / aniWalks.length)];
        else if (Math.abs(velX) > 1 && shiftPressed) currentPlayer = aniRuns[aniTick / (60 / aniRuns.length)];
        else currentPlayer = aniIdles[aniTick / (60 / aniIdles.length)];
    }
    //</editor-fold>

    //<editor-fold desc="Getters/Setters">
    public double getVelX() { return velX; } // Or make velX/velY static if you prefer
    public double getVelY() { return velY; }
    public void setVelX(double val) { velX = val; }
    public void setVelY(double val) { velY = val; }
    public void setIsGrounded(boolean grounded) { isGrounded = grounded; }

    public void setXPosition(float totalX) {
        // Distribute total position back into your delta/bound split variables
        if (xBound > 0 && xBound < 100) {
            xDelta = totalX - xBound;
        } else {
            xBound = 0;
            xDelta = totalX;
        }
        bounds.x = totalX;
    }

    public void setYPosition(float totalY) {
        if (yBound > 0 && yBound < 120) {
            yDelta = totalY - yBound;
        } else {
            yBound = 0;
            yDelta = totalY;
        }
        bounds.y = totalY;
    }
    //</editor-fold>

    //<editor-fold desc="Inputs">
    public void Up(boolean isTrue) {
        upPressed = isTrue;
    }

    public void Down(boolean isTrue) {
        downPressed = isTrue;
    }

    public void Left(boolean isTrue) {
        leftPressed = isTrue;
        aniTick = 0;
    }

    public void Right(boolean isTrue) {
        rightPressed = isTrue;
        aniTick = 0;
    }

    public void Jump(boolean isTrue) {
        jumpPressed = isTrue;
        if (!isTrue) velY = velY * 0.5;
    }

    public void Sprint(boolean isTrue) {
        shiftPressed = isTrue;
        if (isTrue) MAX_SPEED = 6.0;
        else MAX_SPEED = 3.0;
        aniTick = 0;
    }

    public void Click(float posX, float posY) {
        xBound = posX;
        yBound = posY;
    }
    //</editor-fold>
}
