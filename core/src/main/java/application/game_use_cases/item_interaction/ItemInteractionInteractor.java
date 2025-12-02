package application.game_use_cases.item_interaction;

import application.game_use_cases.dismount_entity.DismountEntityInputBoundary;
import application.game_use_cases.dismount_entity.DismountEntityInputData;
import application.game_use_cases.mount_entity.MountEntityInputBoundary;
import application.game_use_cases.mount_entity.MountEntityInputData;
import application.game_use_cases.mount_entity.MountEntityOutputData;
import domain.GamePosition;
import domain.entities.Entity;
import domain.entities.PickupEntity;
import domain.entities.Train;
import domain.items.FuelItem;
import domain.player.Inventory;
import domain.player.InventorySlot;
import domain.player.Player;
import domain.repositories.EntityStorage;

public class ItemInteractionInteractor implements ItemInteractionInputBoundary {

    private final EntityStorage entityStorage;
    private final Player player;
    private final MountEntityInputBoundary mountEntity;
    private final DismountEntityInputBoundary dismountEntity;

    private PickupEntity currentPickupTarget;
    private Train currentTrainTarget;
    private String currentMessage = "";

    public ItemInteractionInteractor(EntityStorage entityStorage,
                                     Player player,
                                     MountEntityInputBoundary mountEntity,
                                     DismountEntityInputBoundary dismountEntity) {
        this.entityStorage = entityStorage;
        this.player = player;
        this.mountEntity = mountEntity;
        this.dismountEntity = dismountEntity;
    }

    @Override
    public ItemInteractionOutputData execute(ItemInteractionInputData inputData) {
        switch (inputData.getActionType()) {
            case REFRESH_TARGET:
                refreshTargets();
                return new ItemInteractionOutputData(currentMessage, null, false, false, false);

            case ACTION_KEY_PRESSED:
                return handleActionKeyPressed();

            case RIDE_KEY_PRESSED:
                return handleRideKeyPressed();

            case DROP_KEY_PRESSED:
                handleDropKeyPressed();
                refreshTargets();
                return new ItemInteractionOutputData(currentMessage, null, false, false, false);

            default:
                return new ItemInteractionOutputData(currentMessage, null, false, false, false);
        }
    }

    // ===================== Core logic =====================

    private ItemInteractionOutputData handleActionKeyPressed() {
        boolean fueled = attemptFuelTrain(currentTrainTarget);
        Integer removedId = null;

        if (fueled) {
            refreshTargets();
            return new ItemInteractionOutputData(currentMessage, null, true, false, false);
        }

        if (currentPickupTarget != null) {
            removedId = attemptPickup(currentPickupTarget);
        }

        refreshTargets();
        return new ItemInteractionOutputData(currentMessage, removedId, false, false, false);
    }

    private ItemInteractionOutputData handleRideKeyPressed() {
        boolean mounted = false;
        boolean dismounted = false;

        if (player.getCurrentRide() != null) {
            // Dismount
            dismountEntity.execute(new DismountEntityInputData());
            dismounted = true;
        } else if (currentTrainTarget != null) {
            // Mount
            MountEntityOutputData out = mountEntity.execute(new MountEntityInputData(currentTrainTarget)
            );
            mounted = out.isMountSuccess();
        }

        refreshTargets();
        return new ItemInteractionOutputData(currentMessage, null, false, mounted, dismounted);
    }

    private void handleDropKeyPressed() {
        player.drop();
    }

    private void refreshTargets() {
        clearTargets();
        StringBuilder sb = new StringBuilder();

        Train train = findTrainInFront();
        if (player.getCurrentRide() != null) {
            sb.append("Press F to dismount");
        } else if (train != null) {
            currentTrainTarget = train;
            sb.append("Press F to drive Train");
            if (isHoldingFuel()) {
                sb.append("\nPress E to fuel Train");
            }
        }

        if (train == null) {
            PickupEntity pickup = findPickupInFront();
            if (pickup != null) {
                currentPickupTarget = pickup;
                String itemName = pickup.getItem().getName();
                sb.append("Press E to pick up ").append(itemName);
            }
        }

        currentMessage = sb.toString();
    }

    private void clearTargets() {
        currentPickupTarget = null;
        currentTrainTarget = null;
        currentMessage = "";
    }

    // ===================== Helper logic from old interactor =====================

    private boolean isInFront(GamePosition from,
                              GamePosition forward,
                              GamePosition targetPosition,
                              float maxDistance,
                              float angle) {

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

    private PickupEntity findPickupInFront() {
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

    private Train findTrainInFront() {
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

    private boolean isHoldingFuel() {
        Inventory inv = player.getInventory();
        InventorySlot slot = inv.getSlot(player.getCurrentSlot());

        if (slot == null || slot.isEmpty()) return false;
        return slot.getItem() instanceof FuelItem;
    }

    private Integer attemptPickup(PickupEntity target) {
        if (target == null || findPickupInFront() != target || !(player.pickUp(target.getItem()))) return null;

        int pickedID = target.getID();
        entityStorage.removeEntity(pickedID);
        return pickedID;
    }

    private boolean attemptFuelTrain(Train train) {
        if (train == null || !isHoldingFuel() || findTrainInFront() != train || train.getCurrentFuel() >= train.getMaxFuel()) return false;

        InventorySlot slot = player.getInventory().getSlot(player.getCurrentSlot());
        FuelItem fuel = (FuelItem) slot.getItem();

        train.addFuel(fuel.getFuelValue());
        player.drop();
        return true;
    }
}
