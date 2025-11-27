package application.use_cases.pickup;

import domain.GamePosition;
import domain.entities.PickupStorage;
import domain.entities.WorldPickup;
import domain.items.Item;
import domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PickupInteractor.
 * Tests finding items in range/angle and attempting pickups.
 */
class PickupInteractorTest {

    private PickupStorage pickupStorage;
    private PickupInteractor interactor;
    private Player player;

    @BeforeEach
    void setUp() {
        pickupStorage = new PickupStorage();
        interactor = new PickupInteractor(pickupStorage);
        player = new Player(new GamePosition(0, 0, 0));
    }

    @Nested
    @DisplayName("findPickupInFront Tests")
    class FindPickupInFrontTests {

        @Test
        @DisplayName("Should find pickup directly in front within range")
        void shouldFindPickupDirectlyInFront() {
            Item testItem = new Item("Test Item", false);
            // Player faces +X by default, place pickup in front
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(2, 0, 0));
            pickupStorage.addPickup(pickup);

            WorldPickup found = interactor.findPickupInFront(player);

            assertNotNull(found, "Should find pickup directly in front");
            assertEquals(testItem, found.getItem(), "Found item should match");
        }

        @Test
        @DisplayName("Should not find pickup behind player")
        void shouldNotFindPickupBehindPlayer() {
            Item testItem = new Item("Test Item", false);
            // Player faces +X, place pickup behind at -X
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(-2, 0, 0));
            pickupStorage.addPickup(pickup);

            WorldPickup found = interactor.findPickupInFront(player);

            assertNull(found, "Should not find pickup behind player");
        }

        @Test
        @DisplayName("Should not find pickup beyond max distance")
        void shouldNotFindPickupBeyondMaxDistance() {
            Item testItem = new Item("Test Item", false);
            // Max distance is 4.5f, place pickup at 5 units
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(5, 0, 0));
            pickupStorage.addPickup(pickup);

            WorldPickup found = interactor.findPickupInFront(player);

            assertNull(found, "Should not find pickup beyond max distance");
        }

        @Test
        @DisplayName("Should find pickup at edge of max distance")
        void shouldFindPickupAtEdgeOfMaxDistance() {
            Item testItem = new Item("Test Item", false);
            // Max distance is 4.5f, place pickup at 4.4 units (within range)
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(4.4f, 0, 0));
            pickupStorage.addPickup(pickup);

            WorldPickup found = interactor.findPickupInFront(player);

            assertNotNull(found, "Should find pickup at edge of max distance");
        }

        @Test
        @DisplayName("Should not find pickup outside view angle")
        void shouldNotFindPickupOutsideViewAngle() {
            Item testItem = new Item("Test Item", false);
            // Player faces +X (direction 1,0,0), max angle is 25 degrees
            // Place pickup at roughly 30 degrees off center (should be outside)
            // tan(30) ~= 0.577, so at distance 2, offset z ~= 1.15
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(2, 0, 1.5f));
            pickupStorage.addPickup(pickup);

            WorldPickup found = interactor.findPickupInFront(player);

            assertNull(found, "Should not find pickup outside view angle");
        }

        @Test
        @DisplayName("Should find pickup at edge of view angle")
        void shouldFindPickupAtEdgeOfViewAngle() {
            Item testItem = new Item("Test Item", false);
            // Max angle is 25 degrees, cos(25) ~= 0.906
            // At distance 2 in X, a z offset of about 0.8 would be within range
            // tan(20) ~= 0.364, so z ~= 0.73 should be within the 25 degree cone
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(2, 0, 0.7f));
            pickupStorage.addPickup(pickup);

            WorldPickup found = interactor.findPickupInFront(player);

            assertNotNull(found, "Should find pickup at edge of view angle");
        }

        @Test
        @DisplayName("Should find closest pickup when multiple in range")
        void shouldFindClosestPickupWhenMultipleInRange() {
            Item farItem = new Item("Far Item", false);
            Item closeItem = new Item("Close Item", false);

            WorldPickup farPickup = new WorldPickup(farItem, new GamePosition(3, 0, 0));
            WorldPickup closePickup = new WorldPickup(closeItem, new GamePosition(1.5f, 0, 0));

            pickupStorage.addPickup(farPickup);
            pickupStorage.addPickup(closePickup);

            WorldPickup found = interactor.findPickupInFront(player);

            assertNotNull(found, "Should find a pickup");
            assertEquals(closeItem, found.getItem(), "Should find the closest pickup");
        }

        @Test
        @DisplayName("Should return null when no pickups exist")
        void shouldReturnNullWhenNoPickups() {
            WorldPickup found = interactor.findPickupInFront(player);

            assertNull(found, "Should return null when no pickups exist");
        }

        @Test
        @DisplayName("Should not find pickup at exact player position")
        void shouldNotFindPickupAtPlayerPosition() {
            Item testItem = new Item("Test Item", false);
            // Pickup at exact player position (distance = 0, but that's still "in front"?)
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(0, 0, 0));
            pickupStorage.addPickup(pickup);

            WorldPickup found = interactor.findPickupInFront(player);

            // When distance is 0, the normalized direction becomes zero vector
            // dot product will be 0 which is less than cos(25) ~= 0.906
            // So it should NOT be found
            assertNull(found, "Should not find pickup at exact player position");
        }

        @Test
        @DisplayName("Should handle pickup at different Y level within range")
        void shouldHandlePickupAtDifferentYLevel() {
            Item testItem = new Item("Test Item", false);
            // Pickup in front but slightly above player
            // Player direction is (1,0,0) by default, so Y offset affects the angle
            // The direction to pickup (2,1,0) normalized is roughly (0.89, 0.45, 0)
            // Dot product with player direction (1,0,0) is 0.89, which is > cos(25) ~= 0.906
            // Actually 0.89 < 0.906, so it would NOT be found
            // This test documents that pickups outside the view cone (due to Y offset) are not found
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(2, 1, 0));
            pickupStorage.addPickup(pickup);

            WorldPickup found = interactor.findPickupInFront(player);

            // Due to the view angle check, a pickup with Y offset might be outside the view cone
            // This tests the actual behavior of the implementation
            // With direction (2,1,0), normalized ~= (0.894, 0.447, 0), dot product with (1,0,0) ~= 0.894
            // cos(25 degrees) ~= 0.906, so 0.894 < 0.906 means it's outside the view cone
            assertNull(found, "Pickup at different Y level may be outside view cone");
        }
    }

    @Nested
    @DisplayName("attemptPickup Tests")
    class AttemptPickupTests {

        @Test
        @DisplayName("Should pick up item and add to player inventory")
        void shouldPickUpItemAndAddToInventory() {
            Item testItem = new Item("Test Item", false);
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(2, 0, 0));
            pickupStorage.addPickup(pickup);

            interactor.attemptPickup(player);

            // Item should be in inventory - check by looking at slots
            boolean foundItem = false;
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                if (!player.getInventory().getSlot(i).isEmpty() &&
                    player.getInventory().getSlot(i).getItem().equals(testItem)) {
                    foundItem = true;
                    break;
                }
            }
            assertTrue(foundItem, "Item should be added to player inventory");
        }

        @Test
        @DisplayName("Should remove pickup from storage after picking up")
        void shouldRemovePickupFromStorage() {
            Item testItem = new Item("Test Item", false);
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(2, 0, 0));
            pickupStorage.addPickup(pickup);

            assertEquals(1, pickupStorage.getAll().size(), "Should have one pickup before");

            interactor.attemptPickup(player);

            assertEquals(0, pickupStorage.getAll().size(), "Should have no pickups after");
        }

        @Test
        @DisplayName("Should do nothing when no pickup in front")
        void shouldDoNothingWhenNoPickupInFront() {
            // No pickups added

            interactor.attemptPickup(player);

            // Should not throw, just do nothing
            assertEquals(0, pickupStorage.getAll().size(), "Storage should remain empty");
        }

        @Test
        @DisplayName("Should pick up closest item when multiple in range")
        void shouldPickUpClosestItem() {
            Item farItem = new Item("Far Item", false);
            Item closeItem = new Item("Close Item", false);

            WorldPickup farPickup = new WorldPickup(farItem, new GamePosition(3, 0, 0));
            WorldPickup closePickup = new WorldPickup(closeItem, new GamePosition(1.5f, 0, 0));

            pickupStorage.addPickup(farPickup);
            pickupStorage.addPickup(closePickup);

            interactor.attemptPickup(player);

            // Close item should be picked up - check by looking at slots
            boolean foundCloseItem = false;
            boolean foundFarItem = false;
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                if (!player.getInventory().getSlot(i).isEmpty()) {
                    Item item = player.getInventory().getSlot(i).getItem();
                    if (item.equals(closeItem)) foundCloseItem = true;
                    if (item.equals(farItem)) foundFarItem = true;
                }
            }
            assertTrue(foundCloseItem, "Closest item should be picked up");
            // Far item should still be in storage
            assertEquals(1, pickupStorage.getAll().size(), "One pickup should remain");
            assertFalse(foundFarItem, "Far item should not be picked up");
        }

        @Test
        @DisplayName("Should only pick up one item per attempt")
        void shouldOnlyPickUpOneItemPerAttempt() {
            Item item1 = new Item("Item 1", false);
            Item item2 = new Item("Item 2", false);

            WorldPickup pickup1 = new WorldPickup(item1, new GamePosition(1.5f, 0, 0));
            WorldPickup pickup2 = new WorldPickup(item2, new GamePosition(2, 0, 0));

            pickupStorage.addPickup(pickup1);
            pickupStorage.addPickup(pickup2);

            interactor.attemptPickup(player);

            // Only one item should be picked up - check by looking at slots
            int pickedUp = 0;
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                if (!player.getInventory().getSlot(i).isEmpty()) {
                    Item item = player.getInventory().getSlot(i).getItem();
                    if (item.equals(item1) || item.equals(item2)) pickedUp++;
                }
            }

            assertEquals(1, pickedUp, "Only one item should be picked up per attempt");
            assertEquals(1, pickupStorage.getAll().size(), "One pickup should remain");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle very small distances")
        void shouldHandleVerySmallDistances() {
            Item testItem = new Item("Test Item", false);
            // Very close pickup
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(0.1f, 0, 0));
            pickupStorage.addPickup(pickup);

            WorldPickup found = interactor.findPickupInFront(player);

            assertNotNull(found, "Should find very close pickup");
        }

        @Test
        @DisplayName("Should handle negative coordinates")
        void shouldHandleNegativeCoordinates() {
            // Create player at negative coordinates
            player = new Player(new GamePosition(-10, -5, -10));

            Item testItem = new Item("Test Item", false);
            // Pickup in front of player (player faces +X by default)
            WorldPickup pickup = new WorldPickup(testItem, new GamePosition(-8, -5, -10));
            pickupStorage.addPickup(pickup);

            interactor = new PickupInteractor(pickupStorage);

            WorldPickup found = interactor.findPickupInFront(player);

            assertNotNull(found, "Should find pickup with negative coordinates");
        }

        @Test
        @DisplayName("Should handle large number of pickups")
        void shouldHandleLargeNumberOfPickups() {
            // Add many pickups
            for (int i = 0; i < 100; i++) {
                Item item = new Item("Item " + i, false);
                // Place most pickups out of range
                WorldPickup pickup = new WorldPickup(item, new GamePosition(10 + i, i, i));
                pickupStorage.addPickup(pickup);
            }

            // Add one in range
            Item targetItem = new Item("Target Item", false);
            WorldPickup targetPickup = new WorldPickup(targetItem, new GamePosition(2, 0, 0));
            pickupStorage.addPickup(targetPickup);

            WorldPickup found = interactor.findPickupInFront(player);

            assertNotNull(found, "Should find the one in-range pickup");
            assertEquals(targetItem, found.getItem(), "Should find correct item");
        }
    }
}
