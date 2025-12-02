package application.game_use_cases.item_interaction;

import domain.GamePosition;
import domain.entities.PickupEntity;
import domain.player.Player;
import domain.repositories.EntityStorage;

public class PickupInteractor {

    private static final float PICKUP_MAX_DISTANCE = 4.5f;
    private static final float PICKUP_MAX_ANGLE_DEG = 25f;

    private final EntityStorage entityStorage;
    private final Player player;
    private final EntityTargetingService targetingService;

    public PickupInteractor(EntityStorage entityStorage,
                            Player player) {
        this.entityStorage = entityStorage;
        this.player = player;
        this.targetingService = new EntityTargetingService(entityStorage);
    }

    public PickupInteractor(EntityStorage entityStorage,
                            Player player,
                            EntityTargetingService targetingService) {
        this.entityStorage = entityStorage;
        this.player = player;
        this.targetingService = targetingService;
    }

    /**
     * Find the closest PickupEntity in front of the player, or null.
     */
    public PickupEntity findPickupInFront() {
        GamePosition pos = player.getPosition();
        GamePosition dir = player.getDirection().nor();

        return targetingService.findClosestInFront(
            PickupEntity.class,
            pos,
            dir,
            PICKUP_MAX_DISTANCE,
            PICKUP_MAX_ANGLE_DEG,
            pickup -> true
        );
    }

    /**
     * Try to pick up the item in front.
     * Returns the picked entity's ID, or null if nothing is picked.
     */
    public Integer attemptPickup(PickupEntity target) {
        if (target == null || findPickupInFront() != target) return null;

        boolean added = player.pickUp(target.getItem());
        if (!added) return null;

        int pickedID = target.getID();
        entityStorage.removeEntity(pickedID);
        return pickedID;
    }
}
