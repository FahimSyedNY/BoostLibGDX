package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import github.FahimSyedNY.boost.Main;
import map.Level;

public abstract class Enemy extends Entity {
    Player player;
    protected boolean isDead, inRange, isHeavy;
    protected int range = 192;
    protected TextureRegion[] aniWalks;
    protected Sound thud, splat, squish, poof;

    public Enemy(float x, float y, float width, float height, int health, Player player) {
        super(x, y, width, height, health);
        this.player = player;
        feetLength = -4;
        feet = new Rectangle(bounds.x - feetLength / 2, bounds.y - 2, bounds.width + feetLength, 2);
        thud = Gdx.audio.newSound(Gdx.files.internal("sounds/Thud.mp3"));
        splat = Gdx.audio.newSound(Gdx.files.internal("sounds/SlimeSplat.mp3"));
        squish = Gdx.audio.newSound(Gdx.files.internal("sounds/SlimeSquish.mp3"));
        poof = Gdx.audio.newSound(Gdx.files.internal("sounds/Poof.mp3"));
    }

    public void update() {
        //Physics - Evan, Collisions - Fahim

        if (!isDead) {
            velUpdate();
            inRange = player.getXPlayer() - range < xDelta && xDelta < player.getXPlayer() + range
                && player.getYPlayer() - range < yDelta && yDelta < player.getYPlayer() + range;
            if (!isInvincible) playerHit();
            if (!isInvincible) hitPlayer();
            move();
            if (isInvincible) invDuration();
            animate();
        }
    }

    public void render(SpriteBatch batch) {
        if (!isDead) {
            if (isInvincible) {
                oldColor = batch.getColor();
                batch.setColor(oldColor.r, oldColor.g, oldColor.b, 0.5f);
            }

            if (inRange) {
                renderHearts(batch);
            }

            if (isInvincible) batch.setColor(oldColor.r, oldColor.g * 0.3f, oldColor.b * 0.3f, 1f);

            batch.draw(
                currentFrame,
                xDelta + (bounds.getWidth() - currentFrame.getRegionWidth()) / 2f,
                yDelta,
                currentFrame.getRegionWidth() / 2f,
                currentFrame.getRegionHeight() / 2f,
                currentFrame.getRegionWidth(),
                currentFrame.getRegionHeight(),
                flip, 1.0f,
                0.0f
            );

            if (isInvincible) batch.setColor(oldColor.r, oldColor.g / 0.3f, oldColor.b / 0.3f, 1f);
        }
    }

    public void velUpdate() {
        // Vertical Movement
        if (!isGrounded) {
            velY += gravity;
        } else if (velY < 0) {
            velY = 0;
        }

        if (isGrounded) {
            if (velX > maxSpeed) velX = maxSpeed;
            if (velX < -maxSpeed) velX = -maxSpeed;
        }

        nextXBounds.setPosition((bounds.x + velX), bounds.y);
        nextYBounds.setPosition(bounds.x, (bounds.y + velY));
    }

    public void playerHit() {
        if (inRange) {
            if (player.getVelY() < 0 && bounds.overlaps(player.getFeet())) {
                damage();
                setInvincible();
                player.setVelY(10);
            }
        }
    }

    public void hitPlayer() {
        if (!player.getInvincible() && inRange && bounds.overlaps(player.getBounds())) {
            player.setVelY(5);
            float pushDirection = (velX != 0) ? Math.signum(velX) : 1f;
            player.setVelX(pushDirection * 5);
            player.damage();
            player.setInvincible();
        }
    }


    public void move() {
        if (health > 0) {
            if (inRange && isGrounded) {
                moveX();
                Level.checkHorizontalCollisions(this);

                float distanceX = player.getXPlayer() - xDelta;
                if (distanceX != 0) {
                    velX = Math.abs(velX) * Math.signum(distanceX);
                    if (velX == 0) velX = maxSpeed * Math.signum(distanceX);
                } else {
                    velX = 0;
                }

                if ((int) (Math.random() * 60) == 0 && isGrounded) {
                    isGrounded = false;
                    velY = jumpForce;
                    squish.play();
                    if (isHeavy) {
                        Main.shakeCamera(10f, 0.2f);
                        thud.play();
                    }
                }
            } else {
                if (Level.wouldXCollide(this)) velX *= -1;
                moveX();
            }
        }

        bounds.setPosition(xDelta, yDelta);
        feet.setPosition(bounds.x - feetLength / 2, bounds.y - 2);

        isGrounded = Level.isStandingOnGround(this);

        if (!Level.wouldYCollide(this)) {
            moveY();
            Level.checkVerticalCollisions(this);
        } else velY = 0;
    }

    public void moveX() {
        xDelta += (float) velX;

        if (velX > 0) flip = 1.0f;
        if (velX < 0) flip = -1.0f;

        bounds.setPosition(xDelta, yDelta);
        feet.setPosition(bounds.x - feetLength / 2, bounds.y - 2);
    }

    public void moveY() {
        yDelta += (float) velY;

        if ((yDelta) <= floorY) {
            yDelta = floorY;
            velY = 0;
            isGrounded = true;
        }

        bounds.setPosition(xDelta, yDelta);
        feet.setPosition(bounds.x - feetLength / 2, bounds.y - 2);
    }

    public void animate() {
        aniTick++;
        if (aniTick > 59) aniTick = 0;
        currentFrame = aniWalks[(aniTick * aniWalks.length) / 60];
    }

    public void invDuration() {
        invTick++;
        if (invTick == 60) {
            if (health == 0) {
                isDead = true;

                if (isHeavy) Main.addCollectable(new Heart(xDelta + bounds.getWidth() / 2 - 4.5F, yDelta, player));
                else {
                    int dice = (int) (Math.random() * 6);
                    if (dice > 2) Main.addCollectable(new Coin(xDelta + bounds.getWidth() / 2 - 3F, yDelta, player));
                    if (dice == 0) Main.addCollectable(new Heart(xDelta + bounds.getWidth() / 2 - 4.5F, yDelta, player));
                }
                poof.play();
            }
            isInvincible = false;
        }
    }

    public void damage() {
        if (!isInvincible && health > 0) {
            splat.play();
            health--;
        }
    }
}
