package interface_adapter.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import interface_adapter.controllers.PickupController;

public class PickUpInputAdapter extends InputAdapter {

    private final PickupController pickupController;

    public PickUpInputAdapter(PickupController pickupController) {
        this.pickupController = pickupController;
    }

    public void pollInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            pickupController.onActionKeyPressed();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            pickupController.onRideKeyPressed();
        }
    }
}
