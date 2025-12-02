package application.game_use_cases.item_interaction;

import application.game_use_cases.dismount_entity.DismountEntityInputBoundary;
import application.game_use_cases.dismount_entity.DismountEntityInputData;
import application.game_use_cases.mount_entity.MountEntityInputBoundary;
import application.game_use_cases.mount_entity.MountEntityInputData;
import application.game_use_cases.mount_entity.MountEntityOutputData;
import domain.GamePosition;
import domain.entities.PickupEntity;
import domain.entities.Train;
import domain.items.FuelItem;
import domain.player.Inventory;
import domain.player.InventorySlot;
import domain.player.Player;
import domain.repositories.EntityStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for ItemInteractionInteractor.
 */
public class ItemInteractionInteractorTest {

    private EntityStorage entityStorage;
    private Player player;
    private MountEntityInputBoundary mountEntity;
    private DismountEntityInputBoundary dismountEntity;

    private ItemInteractionInteractor interactor;

    private PickupEntity pickup;
    private Train train;
    private Inventory inventory;
    private InventorySlot slot;
    private FuelItem fuelItem;

    @BeforeEach
    void setUp() {
        entityStorage = mock(EntityStorage.class);
        player = mock(Player.class);
        mountEntity = mock(MountEntityInputBoundary.class);
        dismountEntity = mock(DismountEntityInputBoundary.class);

        pickup = mock(PickupEntity.class);
        train = mock(Train.class);
        inventory = mock(Inventory.class);
        slot = mock(InventorySlot.class);
        fuelItem = mock(FuelItem.class);

        // Default: no entities, so refreshTargets() doesn't blow up
        when(entityStorage.getAllIds()).thenReturn(Collections.emptySet());

        // IMPORTANT: default non-null position & direction for player
        GamePosition defaultPos = new GamePosition(0f, 0f, 0f);
        GamePosition defaultDir = new GamePosition(0f, 0f, 1f);
        when(player.getPosition()).thenReturn(defaultPos);
        when(player.getDirection()).thenReturn(defaultDir);
        when(player.getCurrentRide()).thenReturn(null);

        interactor = new ItemInteractionInteractor(entityStorage, player, mountEntity, dismountEntity);
    }

    /** Helper for constructing input. */
    private ItemInteractionInputData input(ItemInteractionInputData.ActionType type) {
        return new ItemInteractionInputData(type);
    }

    // ========================= PICKUP FLOW =========================

    @Test
    @DisplayName("Refresh target: pickup in front -> pickup prompt is shown")
    void refreshTarget_pickupInFront_setsPickupMessage() {
        // Player position + direction (override default if you want, but not required now)
        GamePosition playerPos = new GamePosition(0f, 0f, 0f);
        GamePosition playerDir = new GamePosition(0f, 0f, 1f);
        when(player.getPosition()).thenReturn(playerPos);
        when(player.getDirection()).thenReturn(playerDir);
        when(player.getCurrentRide()).thenReturn(null);

        // One PickupEntity in front
        GamePosition pickupPos = new GamePosition(0f, 0f, 2f); // within 4.5 distance and narrow angle
        when(pickup.getPosition()).thenReturn(pickupPos);
        when(pickup.getItem()).thenReturn(fuelItem);
        when(fuelItem.getName()).thenReturn("Fuel");

        when(entityStorage.getAllIds()).thenReturn(Collections.singleton(1));
        when(entityStorage.getEntityByID(1)).thenReturn(pickup);

        ItemInteractionOutputData out = interactor.execute(
            input(ItemInteractionInputData.ActionType.REFRESH_TARGET)
        );

        assertNotNull(out);
        assertTrue(out.getMessage().contains("Press E to pick up"),
            "Message should contain pickup prompt.");
        assertTrue(out.getMessage().contains("Fuel"),
            "Message should contain item name.");
    }

    @Test
    @DisplayName("Action key with pickup target: picks up item and removes entity")
    void actionKey_pickup_removesEntityAndReturnsId() {
        // Player position + direction
        GamePosition playerPos = new GamePosition(0f, 0f, 0f);
        GamePosition playerDir = new GamePosition(0f, 0f, 1f);
        when(player.getPosition()).thenReturn(playerPos);
        when(player.getDirection()).thenReturn(playerDir);
        when(player.getCurrentRide()).thenReturn(null);

        // Pickup in front
        GamePosition pickupPos = new GamePosition(0f, 0f, 2f);
        when(pickup.getPosition()).thenReturn(pickupPos);
        when(pickup.getItem()).thenReturn(fuelItem);
        when(pickup.getID()).thenReturn(42);
        when(fuelItem.getName()).thenReturn("Fuel");
        when(player.pickUp(fuelItem)).thenReturn(true);

        when(entityStorage.getAllIds()).thenReturn(Collections.singleton(42));
        when(entityStorage.getEntityByID(42)).thenReturn(pickup);

        // First refresh to set currentPickupTarget
        interactor.execute(input(ItemInteractionInputData.ActionType.REFRESH_TARGET));

        // Then press action key (E)
        ItemInteractionOutputData out = interactor.execute(
            input(ItemInteractionInputData.ActionType.ACTION_KEY_PRESSED)
        );

        assertEquals(42, out.getRemovedPickupEntityId(),
            "Removed entity id should be the pickup id.");
        verify(entityStorage).removeEntity(42);
        verify(player).pickUp(fuelItem);
        assertFalse(out.isFueledTrain(), "This action should not be fueling.");
    }

    // ========================= FUEL TRAIN FLOW =========================

    @Test
    @DisplayName("Action key with fuel in hand and train in front: fuels train and drops fuel")
    void actionKey_fuelTrain_addsFuelAndDropsItem() {
        // Player position + direction
        GamePosition playerPos = new GamePosition(0f, 0f, 0f);
        GamePosition playerDir = new GamePosition(0f, 0f, 1f);
        when(player.getPosition()).thenReturn(playerPos);
        when(player.getDirection()).thenReturn(playerDir);
        when(player.getCurrentRide()).thenReturn(null);

        // Train in front
        GamePosition trainPos = new GamePosition(0f, 0f, 5f); // within 10 distance
        when(train.getPosition()).thenReturn(trainPos);
        when(train.getCurrentFuel()).thenReturn(10f);
        when(train.getMaxFuel()).thenReturn(100f);

        when(entityStorage.getAllIds()).thenReturn(Collections.singleton(1));
        when(entityStorage.getEntityByID(1)).thenReturn(train);

        // Player holding fuel in current slot
        when(player.getInventory()).thenReturn(inventory);
        when(player.getCurrentSlot()).thenReturn(0);
        when(inventory.getSlot(0)).thenReturn(slot);
        when(slot.isEmpty()).thenReturn(false);
        when(slot.getItem()).thenReturn(fuelItem);
        when(fuelItem.getFuelValue()).thenReturn(20); // float to match addFuel(float)

        // First, refresh so the interactor sets currentTrainTarget and the prompt
        interactor.execute(input(ItemInteractionInputData.ActionType.REFRESH_TARGET));

        // Then press action key (E)
        ItemInteractionOutputData out = interactor.execute(
            input(ItemInteractionInputData.ActionType.ACTION_KEY_PRESSED)
        );

        assertTrue(out.isFueledTrain(), "Output should indicate fueling happened.");
        assertNull(out.getRemovedPickupEntityId(), "Fueling does not remove entities.");

        verify(train).addFuel(20f);
        verify(player).drop();
    }

    // ========================= RIDE FLOW (MOUNT / DISMOUNT) =========================

    @Test
    @DisplayName("Ride key when near train and not riding: mounts train via mount use case")
    void rideKey_mountsTrain() {
        // Player position + direction
        GamePosition playerPos = new GamePosition(0f, 0f, 0f);
        GamePosition playerDir = new GamePosition(0f, 0f, 1f);
        when(player.getPosition()).thenReturn(playerPos);
        when(player.getDirection()).thenReturn(playerDir);
        when(player.getCurrentRide()).thenReturn(null);

        // Train in front
        GamePosition trainPos = new GamePosition(0f, 0f, 5f);
        when(train.getPosition()).thenReturn(trainPos);

        when(entityStorage.getAllIds()).thenReturn(Collections.singleton(1));
        when(entityStorage.getEntityByID(1)).thenReturn(train);

        // Need non-null inventory because refreshTargets() calls isHoldingFuel()
        when(player.getInventory()).thenReturn(inventory);
        when(player.getCurrentSlot()).thenReturn(0);
        when(inventory.getSlot(0)).thenReturn(slot);
        when(slot.isEmpty()).thenReturn(true); // not holding fuel

        // Mount use case returns success
        MountEntityOutputData mountOut = mock(MountEntityOutputData.class);
        when(mountOut.isMountSuccess()).thenReturn(true);
        when(mountEntity.execute(any(MountEntityInputData.class))).thenReturn(mountOut);

        // Refresh to set currentTrainTarget
        interactor.execute(input(ItemInteractionInputData.ActionType.REFRESH_TARGET));

        // Press ride key (F)
        ItemInteractionOutputData out = interactor.execute(
            input(ItemInteractionInputData.ActionType.RIDE_KEY_PRESSED)
        );

        assertTrue(out.isMounted(), "Output should indicate mounting happened.");
        assertFalse(out.isDismounted(), "Should not be dismounting here.");

        // Verify mount use case was called with train
        ArgumentCaptor<MountEntityInputData> captor = ArgumentCaptor.forClass(MountEntityInputData.class);
        verify(mountEntity).execute(captor.capture());
        assertSame(train, captor.getValue().getRide(),
            "Mount input should contain the current train target.");
    }

    @Test
    @DisplayName("Ride key when already riding: dismounts via dismount use case")
    void rideKey_dismountsWhenRiding() {
        // Player is currently riding something
        when(player.getCurrentRide()).thenReturn(train);

        // refreshTargets() inside handleRideKeyPressed will still call findTrainInFront/findPickupInFront,
        // but we now have default position & direction from setUp(), so no NPE.
        when(entityStorage.getAllIds()).thenReturn(Collections.emptySet());

        ItemInteractionOutputData out = interactor.execute(
            input(ItemInteractionInputData.ActionType.RIDE_KEY_PRESSED)
        );

        assertTrue(out.isDismounted(), "Output should indicate dismount happened.");
        assertFalse(out.isMounted(), "Should not be mounting here.");
        verify(dismountEntity).execute(any(DismountEntityInputData.class));
    }

    // ========================= DROP FLOW =========================

    @Test
    @DisplayName("Drop key: calls player.drop and refreshes targets")
    void dropKey_callsDrop() {
        // No entities, just make sure getAllIds() returns something iterable
        when(entityStorage.getAllIds()).thenReturn(Collections.emptySet());

        // Make sure we have some initial state
        interactor.execute(input(ItemInteractionInputData.ActionType.REFRESH_TARGET));

        ItemInteractionOutputData out = interactor.execute(
            input(ItemInteractionInputData.ActionType.DROP_KEY_PRESSED)
        );

        verify(player).drop();
        assertNotNull(out, "Output should not be null even if drop is simple.");
    }
}
