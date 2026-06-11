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
            case Input.Keys.SPACE -> player.Jump(true);
            case Input.Keys.W -> player.Up(true);
            case Input.Keys.A -> player.Left(true);
            case Input.Keys.S -> player.Down(true);
            case Input.Keys.D -> player.Right(true);
            case Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> player.Sprint(true);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE -> player.Jump(false);
            case Input.Keys.W -> player.Up(false);
            case Input.Keys.A -> player.Left(false);
            case Input.Keys.S -> player.Down(false);
            case Input.Keys.D -> player.Right(false);
            case Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> player.Sprint(false);
            case Input.Keys.C -> Main.isDynaCam();
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
