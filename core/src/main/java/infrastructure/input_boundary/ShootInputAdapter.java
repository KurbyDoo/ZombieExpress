package infrastructure.input_boundary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import presentation.controllers.ShootController;

public class ShootInputAdapter extends InputAdapter {

    private final ShootController shootController;

    public ShootInputAdapter(ShootController shootController) {
        this.shootController = shootController;
    }


    public void pollInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            shootController.onShootKeyPressed();
        }
    }
}
