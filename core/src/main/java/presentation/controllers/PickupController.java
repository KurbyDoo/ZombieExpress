package presentation.controllers;

import application.use_cases.pickup.PickupInteractor;
import domain.entities.PickupEntity;
import domain.player.Player;
import infrastructure.rendering.MeshStorage;

public class PickupController {

    private final Player player;
    private final PickupInteractor interactor;
    private final MeshStorage meshStorage;

    private PickupEntity currentTarget;
    private String currentMessage = "";

    public PickupController(Player player,
                            PickupInteractor interactor,
                            MeshStorage meshStorage) {
        this.player = player;
        this.interactor = interactor;
        this.meshStorage = meshStorage;
    }

    /**
     * Called every frame to update which pickup the player is looking at.
     */
    public void refreshPickupTarget() {
        currentTarget = interactor.findPickupInFront(player);
        if (currentTarget == null) {
            currentMessage = "";
        } else {
            String itemName = currentTarget.getItem().getName();
            currentMessage = "Press E to pick up " + itemName;
        }
    }

    /**
     * Called when the user presses E (from PickUpInputAdapter).
     * Runs the use case and cleans up the mesh if pickup succeeded.
     */
    public void onPickupKeyPressed() {
        Integer pickedID = interactor.attemptPickupInFront(player);
        if (pickedID == null) {
            return;
        }

        // Remove the visual mesh for this entity ID
        meshStorage.removeMesh(pickedID);

        // Clear prompt; next refresh will find a new target (if any)
        currentTarget = null;
        currentMessage = "";
    }

    public String getCurrentPickupMessage() {
        return currentMessage;
    }
}
