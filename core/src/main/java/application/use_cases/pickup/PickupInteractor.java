package application.use_cases.pickup;

import com.badlogic.gdx.math.Vector3;
import domain.entities.Player;
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

        Vector3 playerPosition = player.getPosition();
        Vector3 playerDirection = player.getDirection().nor();

        WorldPickup closestItem = null;
        float closestItemDistance = Float.MAX_VALUE;

        for (WorldPickup pickup : pickupStorage.getAll()) {
            Vector3 pickupDirection = pickup.getPosition().sub(playerPosition);  // from player to pickup
            float distance = pickupDirection.len();

            if (distance > maxDistance) {
                continue;
            }

            Vector3 pickupDirectionNorm = new Vector3(pickupDirection).nor();
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
