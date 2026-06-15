package entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Coin extends Collectable {
    private TextureRegion[] frames;
    private int aniTick;

    public Coin(float x, float y, Player player) {
        super(x, y, 6, 9, player);
        frames = TextureRegion.split(new Texture(Gdx.files.internal("other/CoinShine.png")), 6, 9)[0];
        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/CoinCollect.mp3"));
    }

    @Override
    public void update() {
        if (!collected) {
            if (bounds.overlaps(player.getBounds())) {
                player.addCoin();
                sound.play();
                collected = true;
            }
            lerpY();
            animate();
        }
    }

    public void animate() {
        aniTick++;
        if (aniTick > 59) aniTick = 0;
        currentFrame = frames[(aniTick * frames.length) / 60];
    }
}
