package presentation.controllers;

import application.use_cases.pickup.PickupInteractor;
import domain.entities.PickupEntity;
import domain.entities.Train;
import infrastructure.rendering.MeshStorage;

public class PickupController {

    private final PickupInteractor interactor;
    private final MeshStorage meshStorage;

    private PickupEntity currentPickupTarget;
    private Train currentTrainTarget;
    private String currentMessage = "";

    public PickupController(PickupInteractor interactor,
                            MeshStorage meshStorage) {
        this.interactor = interactor;
        this.meshStorage = meshStorage;
    }

    /**
     * Called every frame to update which pickup the player is looking at.
     */
    public void refreshTarget() {
        clearTargets();
        StringBuilder sb = new StringBuilder();

        Train train = interactor.findTrainInFront();
        if (train != null) {
            currentTrainTarget = train;
            sb.append("Press F to drive Train");
            if (interactor.isHoldingFuel()) {
                sb.append("\nPress E to fuel Train");
            }
        }

        if (train == null) {
            PickupEntity pickup = interactor.findPickupInFront();
            if (pickup != null) {
                currentPickupTarget = pickup;
                String itemName = pickup.getItem().getName();
                sb.append("Press E to pick up ").append(itemName);
            }
        }
        currentMessage = sb.toString();
    }

    /**
     * Called when the user presses E (from PickUpInputAdapter).
     * Runs the use case and cleans up the mesh if pickup succeeded.
     */
    public void onActionKeyPressed() {
        if (currentTrainTarget != null) {
            boolean fueled = interactor.attemptFuelTrain(currentTrainTarget);
            if (fueled) {
                // optional: play sound, log, etc.
                clearTargets();
                return;
            }
        }
        if (currentPickupTarget != null) {
            Integer pickedID = interactor.attemptPickup(currentPickupTarget);
            if (pickedID == null) {
                return;
            }
            // Remove the visual mesh for this entity ID
            meshStorage.removeMesh(pickedID);
            clearTargets();
        }
    }

    private void clearTargets() {
        currentPickupTarget = null;
        currentTrainTarget = null;
        currentMessage = "";
    }

    public String getCurrentPickupMessage() {
        return currentMessage;
    }
}
