package application.use_cases.pickup;

import com.badlogic.gdx.math.Vector3;
import data_access.EntityStorage;
import domain.entities.Entity;
import domain.entities.PickupEntity;
import domain.player.Player;

public class PickupInteractor {

    private final EntityStorage entityStorage;
    private final float maxDistance;
    private final float maxCosAngle;

    public PickupInteractor(EntityStorage entityStorage,
                            float maxDistance,
                            float maxViewAngleDegrees) {
        this.entityStorage = entityStorage;
        this.maxDistance = maxDistance;
        this.maxCosAngle = (float) Math.cos(Math.toRadians(maxViewAngleDegrees));
    }

    /**
     * Find the closest PickupEntity in front of the player, or null.
     */
    public PickupEntity findPickupInFront(Player player) {
        Vector3 playerPos = player.getPosition();
        Vector3 playerDir = player.getDirection().nor();

        PickupEntity best = null;
        float bestDist = Float.MAX_VALUE;

        for (Integer id : entityStorage.getAllIds()) {
            Entity e = entityStorage.getEntityByID(id);
            if (!(e instanceof PickupEntity)) continue;
            if (!e.isVisible()) continue;

            PickupEntity pickup = (PickupEntity) e;
            Vector3 toPickup = new Vector3(pickup.getPosition()).sub(playerPos);
            float distance = toPickup.len();
            if (distance > maxDistance) continue;

            Vector3 toPickupNorm = new Vector3(toPickup).nor();
            float cosAngle = playerDir.dot(toPickupNorm);
            if (cosAngle < maxCosAngle) continue;

            if (distance < bestDist) {
                bestDist = distance;
                best = pickup;
            }
        }

        return best;
    }

    /**
     * Try to pick up the item in front.
     * Returns the picked entity's ID.
     */
    public Integer attemptPickupInFront(Player player) {
        PickupEntity target = findPickupInFront(player);
        if (target == null) {
            return null;
        }
        player.pickUp(target.getItem());
        // Remove entity from the world
        entityStorage.removeEntity(target.getID());
        return target.getID();
    }
}
