package inputs;
import com.badlogic.gdx.math.Vector3;
import entities.Player;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import github.FahimSyedNY.boost.Main;

public class Inputs implements InputProcessor {
    Player player;
    Vector3 touchPoint = new Vector3();

    public Inputs(Player player) {
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE, Input.Keys.DPAD_UP -> player.Jump(true);
            case Input.Keys.W -> player.Up(true);
            case Input.Keys.A, Input.Keys.DPAD_LEFT -> player.Left(true);
            case Input.Keys.S, Input.Keys.DPAD_DOWN -> player.Down(true);
            case Input.Keys.D, Input.Keys.DPAD_RIGHT -> player.Right(true);
            case Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> player.Sprint(true);
            case Input.Keys.LEFT_BRACKET -> Main.cameraZoomUp(true);
            case Input.Keys.RIGHT_BRACKET -> Main.cameraZoomDown(true);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE, Input.Keys.DPAD_UP -> player.Jump(false);
            case Input.Keys.W -> player.Up(false);
            case Input.Keys.A, Input.Keys.DPAD_LEFT -> player.Left(false);
            case Input.Keys.S, Input.Keys.DPAD_DOWN -> player.Down(false);
            case Input.Keys.D, Input.Keys.DPAD_RIGHT -> player.Right(false);
            case Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> player.Sprint(false);
            case Input.Keys.C -> Main.isDynaCam();
            case Input.Keys.LEFT_BRACKET -> Main.cameraZoomUp(false);
            case Input.Keys.RIGHT_BRACKET -> Main.cameraZoomDown(false);
            case Input.Keys.ESCAPE -> Main.setPause();
            case Input.Keys.M -> Main.muteMusic();
        }
        return true;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
