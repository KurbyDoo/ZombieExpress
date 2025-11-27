package application.use_cases.pickup;

import domain.GamePosition;
import domain.player.Player;
import domain.entities.PickupStorage;
import domain.entities.WorldPickup;

public class PickupInteractor {

    private final PickupStorage pickupStorage;

    public PickupInteractor(PickupStorage pickupStorage) {
        this.pickupStorage = pickupStorage;
    }

    /**
     * Find the closest pickup in front of the player, or null if none.
     */
    public WorldPickup findPickupInFront(Player player) {
        float maxDistance = 4.5f;
        float maxCosAngle = (float) Math.cos(Math.toRadians(25));

        GamePosition playerPosition = player.getPosition();
        GamePosition playerDirection = player.getDirection().nor();

        WorldPickup closestItem = null;
        float closestItemDistance = Float.MAX_VALUE;

        for (WorldPickup pickup : pickupStorage.getAll()) {
            GamePosition pickupDirection = pickup.getPosition().sub(playerPosition);  // from player to pickup
            float distance = pickupDirection.len();

            if (distance > maxDistance) {
                continue;
            }

            GamePosition pickupDirectionNorm = new GamePosition(pickupDirection).nor();
            float cosAngle = playerDirection.dot(pickupDirectionNorm);
            if (cosAngle < maxCosAngle) {
                continue;
            }

            if (distance < closestItemDistance) {
                closestItemDistance = distance;
                closestItem = pickup;
            }
        }
        return closestItem;
    }

    /**
     * Try to pick up the item in front of the player, returns true on success.
     */
    public void attemptPickup(Player player) {
        WorldPickup target = findPickupInFront(player);
        if (target == null) return;

        player.pickUp(target.getItem());
        pickupStorage.removePickup(target);
    }
}
