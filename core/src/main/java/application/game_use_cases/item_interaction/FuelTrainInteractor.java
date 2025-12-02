package application.game_use_cases.item_interaction;

import application.game_use_cases.item_interaction.EntityTargetingService;
import domain.GamePosition;
import domain.entities.Train;
import domain.items.FuelItem;
import domain.player.Inventory;
import domain.player.InventorySlot;
import domain.player.Player;
import domain.repositories.EntityStorage;

public class FuelTrainInteractor {

    private static final float TRAIN_MAX_DISTANCE = 10f;
    private static final float TRAIN_MAX_ANGLE_DEG = 50f;

    private final EntityStorage entityStorage;
    private final Player player;
    private final EntityTargetingService targetingService;

    public FuelTrainInteractor(EntityStorage entityStorage,
                               Player player) {
        this.entityStorage = entityStorage;
        this.player = player;
        this.targetingService = new EntityTargetingService(entityStorage);
    }

    public FuelTrainInteractor(EntityStorage entityStorage,
                               Player player,
                               EntityTargetingService targetingService) {
        this.entityStorage = entityStorage;
        this.player = player;
        this.targetingService = targetingService;
    }

    /**
     * Find the closest Train in front of the player, or null.
     */
    public Train findTrainInFront() {
        GamePosition pos = player.getPosition();
        GamePosition dir = player.getDirection().nor();

        return targetingService.findClosestInFront(
            Train.class,
            pos,
            dir,
            TRAIN_MAX_DISTANCE,
            TRAIN_MAX_ANGLE_DEG,
            train -> true
        );
    }

    /**
     * Try to fuel the train in front using the fuel in the current slot.
     * Returns true if fueling succeeded.
     */
    public boolean attemptFuelTrain(Train train) {
        if (train == null) return false;

        // uses Player.isHoldingFuel() that you moved into Player
        if (!isHoldingFuel()) return false;

        // confirm it's actually the train in front
        if (findTrainInFront() != train) {
            return false;
        }

        // now safe to assume active slot contains FuelItem
        InventorySlot slot = player.getInventory().getSlot(player.getCurrentSlot());
        if (slot == null || slot.isEmpty() || !(slot.getItem() instanceof FuelItem)) {
            return false; // extra defensive
        }

        FuelItem fuel = (FuelItem) slot.getItem();

        if (train.getCurrentFuel() >= train.getMaxFuel()) {
            return false;
        }

        train.addFuel(fuel.getFuelValue());
        player.drop(); // consume the fuel item
        return true;
    }

    public boolean isHoldingFuel() {
        Inventory inv = player.getInventory();
        int slotIndex = player.getCurrentSlot();
        InventorySlot slot = inv.getSlot(slotIndex);

        if (slot == null || slot.isEmpty()) return false;
        return slot.getItem() instanceof FuelItem;
    }
}
