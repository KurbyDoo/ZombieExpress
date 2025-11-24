package presentation.controllers;

import application.use_cases.pickup.PickupInteractor;
import domain.player.Player;
import domain.entities.WorldPickup;

public class PickupController {

    private final Player player;
    private final PickupInteractor interactor;

    public PickupController(Player player, PickupInteractor interactor) {
        this.player = player;
        this.interactor = interactor;
    }

    public WorldPickup getCurrentPickupTarget() {
        return interactor.findPickupInFront(player);
    }

    public void onPickupKeyPressed() {
        interactor.attemptPickup(player);
    }
}
