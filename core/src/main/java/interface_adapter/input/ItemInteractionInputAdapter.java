package interface_adapter.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import interface_adapter.controllers.ItemInteractionController;

public class ItemInteractionInputAdapter extends InputAdapter {

    private final ItemInteractionController itemInteractionController;

    public ItemInteractionInputAdapter(ItemInteractionController itemInteractionController) {
        this.itemInteractionController = itemInteractionController;
    }

    public void pollInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            itemInteractionController.onActionKeyPressed();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            itemInteractionController.onRideKeyPressed();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            itemInteractionController.onDropKeyPressed();
        }
    }
}
