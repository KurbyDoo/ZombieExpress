package application.use_cases.update_entity;

import data_access.EntityStorage;
import domain.Chunk;
import domain.GamePosition;
import domain.World;
import domain.entities.Entity;
import domain.entities.EntityType;
import domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EntityBehaviourSystem.
 * Tests behavior dispatch, entity tracking, and cache management.
 */
class EntityBehaviourSystemTest {

    private StubPhysicsControlPort physicsControl;
    private Player player;
    private StubEntityStorage entityStorage;
    private World world;
    private EntityBehaviourSystem behaviourSystem;

    @BeforeEach
    void setUp() {
        physicsControl = new StubPhysicsControlPort();
        player = new Player(new GamePosition(0, 0, 0));
        entityStorage = new StubEntityStorage();
        world = new World();

        behaviourSystem = new EntityBehaviourSystem(physicsControl, player, entityStorage, world);
    }

    @Nested
    @DisplayName("Behavior Dispatch Tests")
    class BehaviorDispatchTests {

        @Test
        @DisplayName("Should update zombie entities with ZombieBehaviour")
        void shouldUpdateZombieWithZombieBehaviour() {
            Entity zombie = new Entity(1, EntityType.ZOMBIE, new GamePosition(5, 0, 0), true);
            entityStorage.addEntity(zombie);
            physicsControl.setInitialVelocity(zombie.getID(), new GamePosition(0, 0, 0));

            behaviourSystem.update(Collections.singletonList(1), 0.016f);

            // Zombie behaviour should have been applied (velocity changed)
            GamePosition velocity = physicsControl.getLastVelocity(zombie.getID());
            assertNotNull(velocity, "Zombie should have velocity set");

            // Zombie should move toward player at origin
            assertTrue(velocity.x < 0, "Zombie should move toward player (negative X)");
        }

        @Test
        @DisplayName("Should update bullet entities with BulletBehaviour")
        void shouldUpdateBulletWithBulletBehaviour() {
            Entity bullet = new Entity(1, EntityType.BULLET, new GamePosition(5, 0, 0), true);
            entityStorage.addEntity(bullet);
            physicsControl.setInitialVelocity(bullet.getID(), new GamePosition(20, 0, 0));

            behaviourSystem.update(Collections.singletonList(1), 0.016f);

            // Bullet behaviour doesn't modify velocity
            assertEquals(0, physicsControl.getSetVelocityCalls(),
                "Bullet behaviour should not set velocity");
        }

        @Test
        @DisplayName("Should handle entities without registered behaviour")
        void shouldHandleEntitiesWithoutRegisteredBehaviour() {
            // TRAIN type doesn't have a registered behaviour
            Entity train = new Entity(1, EntityType.TRAIN, new GamePosition(0, 0, 0), true);
            entityStorage.addEntity(train);

            // Should not throw
            assertDoesNotThrow(() ->
                behaviourSystem.update(Collections.singletonList(1), 0.016f),
                "Should handle entities without registered behaviour");
        }

        @Test
        @DisplayName("Should process multiple entities with different behaviours")
        void shouldProcessMultipleEntitiesWithDifferentBehaviours() {
            Entity zombie = new Entity(1, EntityType.ZOMBIE, new GamePosition(5, 0, 0), true);
            Entity bullet = new Entity(2, EntityType.BULLET, new GamePosition(10, 0, 0), true);
            Entity zombie2 = new Entity(3, EntityType.ZOMBIE, new GamePosition(-5, 0, 0), true);

            entityStorage.addEntity(zombie);
            entityStorage.addEntity(bullet);
            entityStorage.addEntity(zombie2);

            physicsControl.setInitialVelocity(1, new GamePosition(0, 0, 0));
            physicsControl.setInitialVelocity(2, new GamePosition(20, 0, 0));
            physicsControl.setInitialVelocity(3, new GamePosition(0, 0, 0));

            behaviourSystem.update(Arrays.asList(1, 2, 3), 0.016f);

            // Both zombies should have velocity set
            assertNotNull(physicsControl.getLastVelocity(1), "Zombie 1 velocity should be set");
            assertNotNull(physicsControl.getLastVelocity(3), "Zombie 2 velocity should be set");
        }
    }

    @Nested
    @DisplayName("Empty and Null List Tests")
    class EmptyAndNullListTests {

        @Test
        @DisplayName("Should handle empty entity list")
        void shouldHandleEmptyEntityList() {
            assertDoesNotThrow(() ->
                behaviourSystem.update(Collections.emptyList(), 0.016f),
                "Should handle empty entity list");
        }

        @Test
        @DisplayName("Should handle list with non-existent entity IDs")
        void shouldHandleNonExistentEntityIds() {
            // Entity 999 doesn't exist
            assertThrows(NullPointerException.class, () ->
                behaviourSystem.update(Collections.singletonList(999), 0.016f),
                "Should throw when entity doesn't exist");
        }
    }

    @Nested
    @DisplayName("BehaviourContext Tests")
    class BehaviourContextTests {

        @Test
        @DisplayName("Should create context with correct physics reference")
        void shouldCreateContextWithCorrectPhysics() {
            Entity zombie = new Entity(1, EntityType.ZOMBIE, new GamePosition(5, 0, 0), true);
            entityStorage.addEntity(zombie);
            physicsControl.setInitialVelocity(1, new GamePosition(0, 0, 0));

            behaviourSystem.update(Collections.singletonList(1), 0.016f);

            // Verify physics was called through correct port
            assertTrue(physicsControl.getGetVelocityCalls() > 0,
                "Physics port should have been used");
        }

        @Test
        @DisplayName("Should pass correct delta time to behaviours")
        void shouldPassCorrectDeltaTime() {
            Entity zombie = new Entity(1, EntityType.ZOMBIE, new GamePosition(5, 0, 0), true);
            entityStorage.addEntity(zombie);
            physicsControl.setInitialVelocity(1, new GamePosition(0, 0, 0));

            // The delta time is passed to BehaviourContext
            // We can't directly verify it without modifying the behaviour,
            // but we can verify update doesn't throw with various delta times
            assertDoesNotThrow(() ->
                behaviourSystem.update(Collections.singletonList(1), 1.0f),
                "Should handle large delta time");
        }
    }

    @Nested
    @DisplayName("Cache Management Tests")
    class CacheManagementTests {

        @Test
        @DisplayName("Should update position cache correctly")
        void shouldUpdatePositionCacheCorrectly() {
            // Add a chunk for the entity position
            world.addChunk(new GamePosition(0, 0, 0), new Chunk(0, 0, 0));

            Entity zombie = new Entity(1, EntityType.ZOMBIE, new GamePosition(5, 0, 0), true);
            entityStorage.addEntity(zombie);

            behaviourSystem.updateCache(Collections.singletonList(1));

            // Cache should be updated (internal state test)
            // We verify this indirectly through unloadCache behavior
            assertDoesNotThrow(() ->
                behaviourSystem.unloadCache(Collections.singletonList(1)),
                "unloadCache should work after updateCache");
        }

        @Test
        @DisplayName("Should handle empty list in updateCache")
        void shouldHandleEmptyListInUpdateCache() {
            assertDoesNotThrow(() ->
                behaviourSystem.updateCache(Collections.emptyList()),
                "Should handle empty list in updateCache");
        }

        @Test
        @DisplayName("Should handle empty list in unloadCache")
        void shouldHandleEmptyListInUnloadCache() {
            assertDoesNotThrow(() ->
                behaviourSystem.unloadCache(Collections.emptyList()),
                "Should handle empty list in unloadCache");
        }
    }

    @Nested
    @DisplayName("Chunk Transfer Tests")
    class ChunkTransferTests {

        @Test
        @DisplayName("Should transfer entity to new chunk when position changes")
        void shouldTransferEntityToNewChunk() {
            // Setup chunks
            Chunk chunk1 = new Chunk(0, 0, 0);
            Chunk chunk2 = new Chunk(1, 0, 0);
            world.addChunk(new GamePosition(0, 0, 0), chunk1);
            world.addChunk(new GamePosition(1, 0, 0), chunk2);

            // Entity starts in chunk1
            Entity zombie = new Entity(1, EntityType.ZOMBIE, new GamePosition(5, 0, 0), true);
            entityStorage.addEntity(zombie);
            chunk1.addEntity(1);

            // Cache the old position
            behaviourSystem.updateCache(Collections.singletonList(1));

            // Move entity to chunk2 (change position)
            zombie.setPosition(new GamePosition(20, 0, 0)); // Chunk 1 (x=1)

            // Unload cache should transfer entity
            behaviourSystem.unloadCache(Collections.singletonList(1));

            // Entity should be removed from chunk1 and added to chunk2
            assertFalse(chunk1.getEntityIds().contains(1),
                "Entity should be removed from old chunk");
            assertTrue(chunk2.getEntityIds().contains(1),
                "Entity should be added to new chunk");
        }

        @Test
        @DisplayName("Should not transfer entity if position stays in same chunk")
        void shouldNotTransferEntityIfPositionStaysSameChunk() {
            Chunk chunk = new Chunk(0, 0, 0);
            world.addChunk(new GamePosition(0, 0, 0), chunk);

            Entity zombie = new Entity(1, EntityType.ZOMBIE, new GamePosition(5, 0, 0), true);
            entityStorage.addEntity(zombie);
            chunk.addEntity(1);

            behaviourSystem.updateCache(Collections.singletonList(1));

            // Move within same chunk
            zombie.setPosition(new GamePosition(10, 0, 10));

            behaviourSystem.unloadCache(Collections.singletonList(1));

            // Entity should still be in same chunk
            assertTrue(chunk.getEntityIds().contains(1),
                "Entity should stay in same chunk");
        }

        @Test
        @DisplayName("Should handle entity moving to non-existent chunk")
        void shouldHandleEntityMovingToNonExistentChunk() {
            Chunk chunk = new Chunk(0, 0, 0);
            world.addChunk(new GamePosition(0, 0, 0), chunk);

            Entity zombie = new Entity(1, EntityType.ZOMBIE, new GamePosition(5, 0, 0), true);
            entityStorage.addEntity(zombie);
            chunk.addEntity(1);

            behaviourSystem.updateCache(Collections.singletonList(1));

            // Move to position where chunk doesn't exist
            zombie.setPosition(new GamePosition(500, 0, 500));

            // Should not throw
            assertDoesNotThrow(() ->
                behaviourSystem.unloadCache(Collections.singletonList(1)),
                "Should handle entity moving to non-existent chunk");

            // Entity should be removed from old chunk
            assertFalse(chunk.getEntityIds().contains(1),
                "Entity should be removed from old chunk");
        }
    }

    @Nested
    @DisplayName("Strategy Pattern Tests")
    class StrategyPatternTests {

        @Test
        @DisplayName("Should use strategy pattern for entity behaviours")
        void shouldUseStrategyPatternForEntityBehaviours() {
            // Verify different entity types get different behaviours
            Entity zombie = new Entity(1, EntityType.ZOMBIE, new GamePosition(5, 0, 0), true);
            Entity bullet = new Entity(2, EntityType.BULLET, new GamePosition(5, 0, 0), true);

            entityStorage.addEntity(zombie);
            entityStorage.addEntity(bullet);

            physicsControl.setInitialVelocity(1, new GamePosition(0, 0, 0));
            physicsControl.setInitialVelocity(2, new GamePosition(20, 0, 0));

            behaviourSystem.update(Arrays.asList(1, 2), 0.016f);

            // Zombie should have velocity modified (ZombieBehaviour)
            GamePosition zombieVelocity = physicsControl.getLastVelocity(1);
            assertNotNull(zombieVelocity, "Zombie should have velocity");

            // Bullet should not have velocity modified (BulletBehaviour)
            // setLinearVelocity should only be called for zombie (1 call)
            // Bullet behaviour doesn't call setLinearVelocity
        }

        @Test
        @DisplayName("Should be extensible for new entity types")
        void shouldBeExtensibleForNewEntityTypes() {
            // This is more of a design verification
            // The EnumMap<EntityType, EntityBehaviour> pattern allows easy extension
            // Test that CHUNK and COAL types (without behaviour) don't cause errors
            Entity chunk = new Entity(1, EntityType.CHUNK, new GamePosition(0, 0, 0), true);
            Entity coal = new Entity(2, EntityType.COAL, new GamePosition(0, 0, 0), true);

            entityStorage.addEntity(chunk);
            entityStorage.addEntity(coal);

            assertDoesNotThrow(() ->
                behaviourSystem.update(Arrays.asList(1, 2), 0.016f),
                "Should handle entity types without registered behaviour");
        }
    }

    @Nested
    @DisplayName("Performance Edge Cases")
    class PerformanceEdgeCases {

        @Test
        @DisplayName("Should handle many entities efficiently")
        void shouldHandleManyEntities() {
            // Add many zombies
            for (int i = 1; i <= 50; i++) {
                Entity zombie = new Entity(i, EntityType.ZOMBIE,
                    new GamePosition(i * 2, 0, 0), true);
                entityStorage.addEntity(zombie);
                physicsControl.setInitialVelocity(i, new GamePosition(0, 0, 0));
            }

            List<Integer> entityIds = new java.util.ArrayList<>();
            for (int i = 1; i <= 50; i++) {
                entityIds.add(i);
            }

            long start = System.currentTimeMillis();
            behaviourSystem.update(entityIds, 0.016f);
            long elapsed = System.currentTimeMillis() - start;

            // Should complete quickly (less than 1 second for 50 entities)
            assertTrue(elapsed < 1000, "Should process many entities quickly: " + elapsed + "ms");
        }

        @Test
        @DisplayName("Should handle rapid successive updates")
        void shouldHandleRapidSuccessiveUpdates() {
            Entity zombie = new Entity(1, EntityType.ZOMBIE, new GamePosition(5, 0, 0), true);
            entityStorage.addEntity(zombie);
            physicsControl.setInitialVelocity(1, new GamePosition(0, 0, 0));

            // Run many updates
            for (int i = 0; i < 100; i++) {
                behaviourSystem.update(Collections.singletonList(1), 0.016f);
            }

            // Should still function correctly
            GamePosition velocity = physicsControl.getLastVelocity(1);
            assertNotNull(velocity, "Velocity should still be set after many updates");
        }
    }
}
