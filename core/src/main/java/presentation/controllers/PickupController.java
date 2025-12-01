/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Presentation/Controllers)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package presentation.controllers;

import application.game_use_cases.dismount_entity.DismountEntityInputBoundary;
import application.game_use_cases.dismount_entity.DismountEntityInputData;
import application.game_use_cases.mount_entity.MountEntityInputBoundary;
import application.game_use_cases.mount_entity.MountEntityInputData;
import application.game_use_cases.mount_entity.MountEntityOutputData;
import application.game_use_cases.pickup.PickupInteractor;
import domain.entities.PickupEntity;
import domain.entities.Train;
import domain.player.Player;
import infrastructure.rendering.MeshStorage;

public class PickupController {

    private final PickupInteractor interactor;
    private final MountEntityInputBoundary mountEntity;
    private final DismountEntityInputBoundary dismountEntity;
    private final MeshStorage meshStorage;
    private final Player player;

    private PickupEntity currentPickupTarget;
    private Train currentTrainTarget;
    private String currentMessage = "";

    public PickupController(
        Player player,
        PickupInteractor interactor,
        MountEntityInputBoundary mountEntity,
        DismountEntityInputBoundary dismountEntity,
        MeshStorage meshStorage
    ) {
        this.player = player;
        this.interactor = interactor;
        this.mountEntity = mountEntity;
        this.dismountEntity = dismountEntity;
        this.meshStorage = meshStorage;
    }

    /**
     * Called every frame to update which pickup the player is looking at.
     */
    public void refreshTarget() {
        clearTargets();
        StringBuilder sb = new StringBuilder();

        Train train = interactor.findTrainInFront();
        if (player.getCurrentRide() != null) {
            sb.append("Press F to dismount");
        } else if (train != null) {
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

    public void onRideKeyPressed() {
        if (player.getCurrentRide() != null) {
            dismountEntity.execute(new DismountEntityInputData());
        } else if (currentTrainTarget != null) {
            MountEntityOutputData outputData = mountEntity.execute(
                new MountEntityInputData(currentTrainTarget)
            );
            if (outputData.isMountSuccess()) {
                // optional: play sound, log, etc.
                clearTargets();
            }
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
