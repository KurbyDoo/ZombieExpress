package application.use_cases.pickup;

import com.badlogic.gdx.math.Vector3;
import domain.entities.Entity;
import domain.entities.IdToEntityStorage;
import domain.entities.Train;
import domain.entities.PickupEntity;
import domain.items.FuelItem;
import domain.player.Inventory;
import domain.player.InventorySlot;
import domain.player.Player;

public class PickupInteractor {

    private final IdToEntityStorage entityStorage;
    private final Player player;

    public PickupInteractor(IdToEntityStorage entityStorage,
                            Player player) {
        this.entityStorage = entityStorage;
        this.player = player;
    }

    private boolean isInFront(Vector3 from, Vector3 forward, Vector3 targetPosition, float maxDistance, float angle) {
        float minCosAngle = (float) Math.cos(Math.toRadians(angle));

        Vector3 toTarget = new Vector3(targetPosition).sub(from);
        float distance = toTarget.len();

        if (distance > maxDistance) {
            return false;
        }

        Vector3 forwardNorm = new Vector3(forward).nor();
        Vector3 toTargetNorm = new Vector3(toTarget).nor();

        float cosAngle = forwardNorm.dot(toTargetNorm);
        return cosAngle >= minCosAngle;
    }

    /**
     * Find the closest PickupEntity in front of the player, or null.
     */
    public PickupEntity findPickupInFront() {
        Vector3 pos = player.getPosition();
        Vector3 dir = player.getDirection().nor();

        PickupEntity best = null;
        float closestDistance = Float.MAX_VALUE;

        for (int id : entityStorage.getAllIds()) {
            Entity e = entityStorage.getEntityByID(id);
            if (!(e instanceof PickupEntity)) continue;

            PickupEntity p = (PickupEntity) e;
            Vector3 pickupPos = p.getPosition();
            if (!isInFront(pos, dir, pickupPos, 4.5f, 25)) continue;

            float dist = pickupPos.dst(pos);
            if (dist < closestDistance) {
                closestDistance = dist;
                best = p;
            }
        }
        return best;
    }

    public Train findTrainInFront() {
        Train t = entityStorage.getTrain();
        if (t == null) return null;

        Vector3 pos = player.getPosition();
        Vector3 dir = player.getDirection();
        Vector3 trainPos = t.getPosition();

        if (isInFront(pos, dir, trainPos, 10, 50)) {
            return t;
        } else {
            return null;
        }
    }

    public boolean isHoldingFuel() {
        Inventory inv = player.getInventory();
        int slotIndex = player.getCurrentSlot();
        InventorySlot slot = inv.getSlot(slotIndex);

        if (slot == null || slot.isEmpty()) return false;
        return slot.getItem() instanceof FuelItem;
    }

    /**
     * Try to pick up the item in front.
     * Returns the picked entity's ID.
     */
    public Integer attemptPickup(PickupEntity target) {
        if (target == null) return null;

        if (findPickupInFront() != target) {
            return null;
        }

        boolean added = player.pickUp(target.getItem());
        if (!added) {
            return null;
        }

        int pickedID = target.getID();
        entityStorage.removeEntity(pickedID);
        return pickedID;
    }

    public boolean attemptFuelTrain(Train train) {
        if (train == null) return false;
        if (!isHoldingFuel()) return false;

        Inventory inv = player.getInventory();
        InventorySlot slot = inv.getSlot(player.getCurrentSlot());

        if (slot == null || slot.isEmpty()) return false;
        if (!isHoldingFuel()) return false;

        FuelItem fuel = (FuelItem) slot.getItem();
        if (findTrainInFront() != train) {
            return false;
        }

        if (train.getCurrentFuel() >= train.getMaxFuel()) {
            return false;
        }

        train.addFuel(fuel.getFuelValue());
        player.drop();
        return true;
    }
}
