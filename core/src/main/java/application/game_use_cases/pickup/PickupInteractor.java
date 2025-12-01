/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Interactor Pattern: Implements pickup logic.
 * - Query Pattern: Checks proximity to pickupable entities.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [WARN] SRP: Handles multiple concerns (proximity check, item pickup, fuel handling).
 *   Consider separating proximity logic into its own class.
 * - [N/A] LSP: No interface implemented (VIOLATION - should implement InputBoundary).
 *
 * RECOMMENDED FIX:
 *   Create PickupInputBoundary interface for this interactor.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Wildcard import 'domain.entities.*' not recommended.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Avoid wildcard imports.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.pickup;

import domain.repositories.EntityStorage;
import domain.GamePosition;
import domain.entities.*;
import domain.items.FuelItem;
import domain.player.Inventory;
import domain.player.InventorySlot;
import domain.player.Player;

public class PickupInteractor {

    private final EntityStorage entityStorage;
    private final Player player;

    public PickupInteractor(EntityStorage entityStorage,
                            Player player) {
        this.entityStorage = entityStorage;
        this.player = player;
    }

    private boolean isInFront(GamePosition from, GamePosition forward, GamePosition targetPosition, float maxDistance, float angle) {
        float minCosAngle = (float) Math.cos(Math.toRadians(angle));

        GamePosition toTarget = new GamePosition(targetPosition).sub(from);
        float distance = toTarget.len();

        if (distance > maxDistance) {
            return false;
        }

        GamePosition forwardNorm = new GamePosition(forward).nor();
        GamePosition toTargetNorm = new GamePosition(toTarget).nor();

        float cosAngle = forwardNorm.dot(toTargetNorm);
        return cosAngle >= minCosAngle;
    }

    /**
     * Find the closest PickupEntity in front of the player, or null.
     */
    public PickupEntity findPickupInFront() {
        GamePosition pos = player.getPosition();
        GamePosition dir = player.getDirection().nor();

        PickupEntity best = null;
        float closestDistance = Float.MAX_VALUE;

        for (int id : entityStorage.getAllIds()) {
            Entity e = entityStorage.getEntityByID(id);
            if (!(e instanceof PickupEntity)) continue;

            PickupEntity p = (PickupEntity) e;
            GamePosition pickupPos = p.getPosition();
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
        Train t = null;
        for (int id : entityStorage.getAllIds()) {
            Entity e = entityStorage.getEntityByID(id);
            if (!(e instanceof Train)) continue;
            t = (Train) e;
        }

        if (t == null) return null;

        GamePosition pos = player.getPosition();
        GamePosition dir = player.getDirection();
        GamePosition trainPos = t.getPosition();

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
