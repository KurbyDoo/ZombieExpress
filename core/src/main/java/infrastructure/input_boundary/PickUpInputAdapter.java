package infrastructure.input_boundary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import presentation.controllers.PickupController;

public class PickUpInputAdapter {

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
