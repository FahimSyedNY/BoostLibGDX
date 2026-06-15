package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Heart extends Collectable{
    public Heart(float x, float y, Player player) {
        super(x, y, 9, 9, player);
        currentFrame = TextureRegion.split(new Texture(Gdx.files.internal("other/Hearts.png")), 9, 9)[0][1];
        sound =  Gdx.audio.newSound(Gdx.files.internal("sounds/Heal.mp3"));
    }

    @Override
    public void update() {
        if (!collected) {
            if (bounds.overlaps(player.getBounds())) {
                player.heal();
                collected = true;
                sound.play();
            }
            lerpY();
        }
    }
}
