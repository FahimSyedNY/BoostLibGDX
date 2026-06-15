package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import github.FahimSyedNY.boost.Main;
import map.Level;

public class Player extends Entity {
    //<editor-fold desc="Class Vars">
    private boolean rightPressed, leftPressed, jumpPressed, shiftPressed, downPressed, upPressed, isJumping, isWalking, isRunning;
    private final float ACCELERATION = 0.6F, FRICTION = 0.85F;
    private static float xBound, yBound;
    private final TextureRegion[] aniIdles, aniWalks, aniRuns, aniJumps;
    private float cameraJumpShift, cameraDownShift, cameraUpShift, totalCamShift;
    private int coins, airFrames;
    private final Sound walk, run, jump, hurt;
    //</editor-fold>

    public Player(float x, float y, float width, float height) {
        super(x, y, width, height, 3);

        aniJumps = TextureRegion.split(new Texture(Gdx.files.internal("player/LittleGuyJump.png")), 32, 32)[0];
        aniIdles = TextureRegion.split(new Texture(Gdx.files.internal("player/LittleGuyIdle.png")), 28, 32)[0];
        aniWalks = TextureRegion.split(new Texture(Gdx.files.internal("player/LittleGuyWalk.png")), 32, 32)[0];
        aniRuns = TextureRegion.split(new Texture(Gdx.files.internal("player/LittleGuyRun.png")), 28, 32)[0];
        for (TextureRegion region : aniRuns) {
            region.flip(true, false);
        }

        feetLength = -4;
        feet = new Rectangle(bounds.x - feetLength / 2, bounds.y - 2, bounds.width + feetLength, 2);

        jumpForce = 13F;
        maxSpeed = 3F;
        gravity = -0.6F;

        walk = Gdx.audio.newSound(Gdx.files.internal("sounds/Walk.mp3"));
        run = Gdx.audio.newSound(Gdx.files.internal("sounds/Run.mp3"));
        jump = Gdx.audio.newSound(Gdx.files.internal("sounds/Jump.mp3"));
        hurt = Gdx.audio.newSound(Gdx.files.internal("sounds/Hurt.mp3"));

    }

    public void render(SpriteBatch batch) {
        if (isInvincible) {
            oldColor = batch.getColor();
            batch.setColor(oldColor.r, oldColor.g, oldColor.b, 0.5f);
        }

        renderHearts(batch);

        if (isInvincible) {
            batch.setColor(oldColor.r, oldColor.g * 0.3f, oldColor.b * 0.3f, 1f);
        }

        batch.draw(
            currentFrame,
            xDelta + xBound + (bounds.getWidth() - currentFrame.getRegionWidth()) / 2f,
            yDelta + yBound,
            currentFrame.getRegionWidth() / 2f,
            currentFrame.getRegionHeight() / 2f,
            currentFrame.getRegionWidth(),
            currentFrame.getRegionHeight(),
            flip, 1.0f,
            0.0f
        );

        if (isInvincible) {
            batch.setColor(oldColor.r, oldColor.g / 0.3f, oldColor.b / 0.3f, 1f);
        }
    }

    public void update() {
        velUpdate();

        cameraY();

        if (!Level.wouldXCollide(this)) {
            moveX();
            Level.checkHorizontalCollisions(this);
        } else velX = 0;

        isGrounded = Level.isStandingOnGround(this);

        if (!Level.wouldYCollide(this)) {
            moveY();
            Level.checkVerticalCollisions(this);
        } else velY = 0;

        walkAudio();

        if (isInvincible) invDuration();

        animate();
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

        if (velX > maxSpeed) velX = maxSpeed;
        if (velX < -maxSpeed) velX = -maxSpeed;

//         Vertical Movement
        if (!isGrounded) {
            velY += gravity;
        } else if (velY < 0) {
            velY = 0;
        }

        if (jumpPressed && isGrounded && velY < 13) {
            velY = jumpForce;
            isGrounded = false;
            aniTick = 0;
            if (!Level.wouldYCollide(this)) {
                jump.play();
            }
        }

        if (velY > 12) isJumping = true;

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

    public void moveX() {
        if (xBound > 100) xBound = 100;
        if (velX > 0 && xBound < 100) xBound += (float) velX;
        else if (velX < 0 && xBound > 0) xBound += (float) velX;
        else xDelta += (float) velX;

        if (velX > 0) flip = 1.0f;
        if (velX < 0) flip = -1.0f;

        // Update bounds immediately for X checking
        bounds.setPosition(xDelta + xBound, yDelta + yBound);
        feet.setPosition(bounds.x + 2, bounds.y - 2);
    }

    public void moveY() {
        if (velY > 0 && yBound < 120) yBound += (float) (velY * 0.8);
        else if (velY < 0 && yBound > 0) yBound += (float) velY;
        else if (velY > 0) yDelta += (float) (velY * 0.8);
        else yDelta += (float) velY;

        if ((yDelta + yBound) <= floorY) {
            yDelta = floorY;
            yBound = 0;
            velY = 0;
            isGrounded = true;
        }

        bounds.setPosition(xDelta + xBound, yDelta + yBound);
        feet.setPosition(bounds.x + 2, bounds.y - 2);
    }

    public void walkAudio() {
        if ((leftPressed || rightPressed) && !shiftPressed && !isWalking && isGrounded && velX != 0) {
            walk.loop();
            isWalking = true;
            if (isRunning) {
                isRunning = false;
                run.stop();
            }
        }

        if (!(leftPressed || rightPressed) && isWalking || !isGrounded || velX == 0) {
            walk.stop();
            isWalking = false;
        }

        if ((leftPressed || rightPressed) && shiftPressed && !isRunning && isGrounded && velX != 0) {
            run.loop();
            isRunning = true;
            if (isWalking) {
                isWalking = false;
                walk.stop();
            }
        }

        if (!(leftPressed || rightPressed) && isRunning || !isGrounded || velX == 0) {
            run.stop();
            isRunning = false;
        }
    }

    public void animate() {
        aniTick++;
        if (aniTick > 59 && !shiftPressed) aniTick = 0;
        else if (aniTick > 59) aniTick = 18;

        if (!isJumping) {
            if (Math.abs(velX) > 1 && !shiftPressed) currentFrame = aniWalks[aniTick / (60 / aniWalks.length)];
            else if (Math.abs(velX) > 1 && shiftPressed) currentFrame = aniRuns[aniTick / (60 / aniRuns.length)];
            else currentFrame = aniIdles[aniTick / (60 / aniIdles.length)];
        } else {
            int frame = (int) (5 * (1 - Math.abs(velY) / jumpForce));
            if (frame >= 4) frame = 3;
            if (frame < 0) frame = 0;
            currentFrame = aniJumps[frame];
            if (isGrounded) isJumping = false;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Getters/Setters">
    public float getXDelta() {
        return xDelta;
    }

    public float getYDelta() {
        return yDelta;
    }

    public float getXPlayer() {
        return xDelta + xBound;
    }

    public float getYPlayer() {
        return yDelta + yBound;
    }

    public float getTotalCamShift() {
        return totalCamShift;
    }

    public int getCoins() {
        return coins;
    }

    public void damage() {
        if (!isInvincible && health > 0) {
            health--;
            hurt.play();
        }
        if (health == 0) Main.setPause(true);
    }

    public void addCoin() {
        coins++;
    }

    @Override
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

    @Override
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
        velY += 2;
    }

    public void Down(boolean isTrue) {
        downPressed = isTrue;
        velY -= 2;
    }

    public void Left(boolean isTrue) {
        leftPressed = isTrue;
        if (!isJumping) {
            aniTick = 0;
        }
    }

    public void Right(boolean isTrue) {
        rightPressed = isTrue;
        if (!isJumping) {
            aniTick = 0;
        }
    }

    public void Jump(boolean isTrue) {
        jumpPressed = isTrue;
        if (!isTrue) {
            velY = velY * 0.5F;
        }
    }

    public void Sprint(boolean isTrue) {
        shiftPressed = isTrue;
        if (isTrue) maxSpeed = 6.0F;
        else maxSpeed = 3.0F;
        if (velX > 0 && !isJumping) {
            aniTick = 0;
        }
    }

    public void Click(float posX, float posY) {
        xBound = posX;
        yBound = posY;
    }
    //</editor-fold>
}
