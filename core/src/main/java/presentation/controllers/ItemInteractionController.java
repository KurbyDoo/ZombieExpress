package presentation.controllers;

import application.game_use_cases.item_interaction.ItemInteractionInputBoundary;
import application.game_use_cases.item_interaction.ItemInteractionInputData;
import application.game_use_cases.item_interaction.ItemInteractionInputData.ActionType;
import application.game_use_cases.item_interaction.ItemInteractionOutputData;
import infrastructure.rendering.MeshStorage;

public class ItemInteractionController {

    private final ItemInteractionInputBoundary itemInteractionInteractor;
    private final MeshStorage meshStorage;

    private String currentMessage = "";

    public ItemInteractionController(
        ItemInteractionInputBoundary itemInteractionInteractor,
        MeshStorage meshStorage
    ) {
        this.itemInteractionInteractor = itemInteractionInteractor;
        this.meshStorage = meshStorage;
    }

    /**
     * Called every frame to update which pickup/train the player is looking at.
     */
    public void refreshTarget() {
        ItemInteractionOutputData output = itemInteractionInteractor.execute(
            new ItemInteractionInputData(ActionType.REFRESH_TARGET)
        );
        this.currentMessage = output.getMessage();
    }

    /**
     * Called when the user presses E (from PickUpInputAdapter).
     */
    public void onActionKeyPressed() {
        ItemInteractionOutputData output = itemInteractionInteractor.execute(
            new ItemInteractionInputData(ActionType.ACTION_KEY_PRESSED)
        );

        Integer removedId = output.getRemovedPickupEntityId();
        if (removedId != null) {
            meshStorage.removeMesh(removedId);
        }

        this.currentMessage = output.getMessage();
    }

    /**
     * Called when the user presses F (ride / dismount).
     */
    public void onRideKeyPressed() {
        ItemInteractionOutputData output = itemInteractionInteractor.execute(
            new ItemInteractionInputData(ActionType.RIDE_KEY_PRESSED)
        );
        this.currentMessage = output.getMessage();
    }

    /**
     * Called when the user presses drop key (Q or similar).
     */
    public void onDropKeyPressed() {
        itemInteractionInteractor.execute(
            new ItemInteractionInputData(ActionType.DROP_KEY_PRESSED)
        );
    }

    public String getCurrentPickupMessage() {
        return currentMessage;
    }
}
