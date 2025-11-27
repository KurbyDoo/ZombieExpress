package application.use_cases.update_entity;

import domain.GamePosition;
import domain.entities.Entity;
import domain.entities.EntityType;
import domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BulletBehaviour.
 * Tests bullet behavior (currently minimal as bullets maintain initial velocity).
 */
class BulletBehaviourTest {

    private BulletBehaviour bulletBehaviour;
    private StubPhysicsControlPort physicsControl;
    private Player player;
    private Entity bullet;
    private BehaviourContext context;

    @BeforeEach
    void setUp() {
        bulletBehaviour = new BulletBehaviour();
        physicsControl = new StubPhysicsControlPort();
        player = new Player(new GamePosition(0, 0, 0));
        bullet = new Entity(1, EntityType.BULLET, new GamePosition(5, 0, 0), true);
    }

    private void createContext(float deltaTime) {
        context = new BehaviourContext(physicsControl, player, deltaTime);
    }

    @Nested
    @DisplayName("Basic Update Tests")
    class BasicUpdateTests {

        @Test
        @DisplayName("Should not throw when updating bullet")
        void shouldNotThrowWhenUpdatingBullet() {
            createContext(0.016f);

            assertDoesNotThrow(() -> bulletBehaviour.update(bullet, context),
                "Bullet update should not throw");
        }

        @Test
        @DisplayName("Should not modify velocity (bullets maintain initial velocity)")
        void shouldNotModifyVelocity() {
            // Set initial bullet velocity
            physicsControl.setInitialVelocity(bullet.getID(), new GamePosition(20, 0, 0));
            createContext(0.016f);

            bulletBehaviour.update(bullet, context);

            // Current implementation doesn't modify velocity
            // Check that no setLinearVelocity calls were made
            assertEquals(0, physicsControl.getSetVelocityCalls(),
                "Should not modify velocity for bullets");
        }

        @Test
        @DisplayName("Should not call lookAt")
        void shouldNotCallLookAt() {
            createContext(0.016f);

            bulletBehaviour.update(bullet, context);

            assertEquals(0, physicsControl.getLookAtCalls(),
                "Should not call lookAt for bullets");
        }
    }

    @Nested
    @DisplayName("Context Access Tests")
    class ContextAccessTests {

        @Test
        @DisplayName("Should have access to physics port through context")
        void shouldHaveAccessToPhysicsPort() {
            createContext(0.016f);

            assertNotNull(context.physics, "Physics should be accessible");
            assertSame(physicsControl, context.physics, "Physics should be the stub");
        }

        @Test
        @DisplayName("Should have access to player through context")
        void shouldHaveAccessToPlayer() {
            createContext(0.016f);

            assertNotNull(context.player, "Player should be accessible");
            assertSame(player, context.player, "Player should be the test player");
        }

        @Test
        @DisplayName("Should have access to deltaTime through context")
        void shouldHaveAccessToDeltaTime() {
            createContext(0.5f);

            assertEquals(0.5f, context.deltaTime, "DeltaTime should be accessible");
        }
    }

    @Nested
    @DisplayName("Bullet State Tests")
    class BulletStateTests {

        @Test
        @DisplayName("Should preserve bullet position")
        void shouldPreserveBulletPosition() {
            GamePosition initialPosition = bullet.getPosition();
            createContext(0.016f);

            bulletBehaviour.update(bullet, context);

            // Position should not be modified by behaviour
            // (physics engine handles actual movement)
            assertEquals(initialPosition.x, bullet.getPosition().x, 0.01f);
            assertEquals(initialPosition.y, bullet.getPosition().y, 0.01f);
            assertEquals(initialPosition.z, bullet.getPosition().z, 0.01f);
        }

        @Test
        @DisplayName("Should preserve bullet visibility")
        void shouldPreserveBulletVisibility() {
            bullet.setVisible(true);
            createContext(0.016f);

            bulletBehaviour.update(bullet, context);

            assertTrue(bullet.isVisible(), "Visibility should be preserved");
        }

        @Test
        @DisplayName("Should work with invisible bullets")
        void shouldWorkWithInvisibleBullets() {
            bullet.setVisible(false);
            createContext(0.016f);

            assertDoesNotThrow(() -> bulletBehaviour.update(bullet, context),
                "Should handle invisible bullets");
        }
    }

    @Nested
    @DisplayName("Multiple Bullets Tests")
    class MultipleBulletsTests {

        @Test
        @DisplayName("Should handle multiple bullet entities independently")
        void shouldHandleMultipleBulletsIndependently() {
            Entity bullet1 = new Entity(1, EntityType.BULLET, new GamePosition(0, 0, 0), true);
            Entity bullet2 = new Entity(2, EntityType.BULLET, new GamePosition(10, 0, 0), true);
            Entity bullet3 = new Entity(3, EntityType.BULLET, new GamePosition(20, 0, 0), true);

            physicsControl.setInitialVelocity(bullet1.getID(), new GamePosition(20, 0, 0));
            physicsControl.setInitialVelocity(bullet2.getID(), new GamePosition(-20, 0, 0));
            physicsControl.setInitialVelocity(bullet3.getID(), new GamePosition(0, 0, 20));

            createContext(0.016f);

            // Update all bullets
            bulletBehaviour.update(bullet1, context);
            bulletBehaviour.update(bullet2, context);
            bulletBehaviour.update(bullet3, context);

            // All should complete without error
            assertEquals(0, physicsControl.getSetVelocityCalls(),
                "Bullets should not modify velocity");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle zero delta time")
        void shouldHandleZeroDeltaTime() {
            createContext(0f);

            assertDoesNotThrow(() -> bulletBehaviour.update(bullet, context),
                "Should handle zero delta time");
        }

        @Test
        @DisplayName("Should handle large delta time")
        void shouldHandleLargeDeltaTime() {
            createContext(1.0f);

            assertDoesNotThrow(() -> bulletBehaviour.update(bullet, context),
                "Should handle large delta time");
        }

        @Test
        @DisplayName("Should handle bullet at origin")
        void shouldHandleBulletAtOrigin() {
            bullet = new Entity(1, EntityType.BULLET, new GamePosition(0, 0, 0), true);
            createContext(0.016f);

            assertDoesNotThrow(() -> bulletBehaviour.update(bullet, context),
                "Should handle bullet at origin");
        }

        @Test
        @DisplayName("Should handle bullet with negative coordinates")
        void shouldHandleBulletWithNegativeCoordinates() {
            bullet = new Entity(1, EntityType.BULLET, new GamePosition(-100, -50, -200), true);
            createContext(0.016f);

            assertDoesNotThrow(() -> bulletBehaviour.update(bullet, context),
                "Should handle negative coordinates");
        }

        @Test
        @DisplayName("Should handle bullet far from player")
        void shouldHandleBulletFarFromPlayer() {
            bullet = new Entity(1, EntityType.BULLET, new GamePosition(10000, 0, 10000), true);
            createContext(0.016f);

            assertDoesNotThrow(() -> bulletBehaviour.update(bullet, context),
                "Should handle bullet far from player");
        }
    }

    @Nested
    @DisplayName("Interface Contract Tests")
    class InterfaceContractTests {

        @Test
        @DisplayName("Should implement EntityBehaviour interface")
        void shouldImplementEntityBehaviourInterface() {
            assertTrue(bulletBehaviour instanceof EntityBehaviour,
                "BulletBehaviour should implement EntityBehaviour");
        }

        @Test
        @DisplayName("Should have update method that accepts Entity and BehaviourContext")
        void shouldHaveCorrectUpdateSignature() {
            // This compiles, so the method signature is correct
            EntityBehaviour behaviour = bulletBehaviour;
            createContext(0.016f);

            assertDoesNotThrow(() -> behaviour.update(bullet, context),
                "Update method should work through interface");
        }
    }
}
