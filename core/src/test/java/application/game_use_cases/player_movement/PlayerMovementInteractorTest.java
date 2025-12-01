/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Test
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package application.game_use_cases.player_movement;

import domain.GamePosition;
import domain.entities.Train;
import domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PlayerMovementInteractor.
 * Tests rotation updates, ground movement velocity, train velocity, and sprint mode.
 */
class PlayerMovementInteractorTest {

    private Player player;
    private PlayerMovementInteractor interactor;

    @BeforeEach
    void setUp() {
        player = new Player(new GamePosition(0, 0, 0));
        interactor = new PlayerMovementInteractor(player);
    }

    @Nested
    @DisplayName("Rotation Tests")
    class RotationTests {

        @Test
        @DisplayName("Should update player rotation when deltaX is non-zero")
        void shouldUpdateRotationWithDeltaX() {
            GamePosition initialDirection = player.getDirection();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                false, false, false, false, false,
                10f, 0f, // deltaX, deltaY
                0.016f   // deltaTime
            );

            interactor.execute(inputData);

            GamePosition newDirection = player.getDirection();
            // Direction should have changed due to rotation
            assertFalse(initialDirection.epsilonEquals(newDirection, 0.001f),
                "Direction should change when rotating horizontally");
        }

        @Test
        @DisplayName("Should update player rotation when deltaY is non-zero")
        void shouldUpdateRotationWithDeltaY() {
            GamePosition initialDirection = player.getDirection();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                false, false, false, false, false,
                0f, 10f, // deltaX, deltaY
                0.016f
            );

            interactor.execute(inputData);

            GamePosition newDirection = player.getDirection();
            assertFalse(initialDirection.epsilonEquals(newDirection, 0.001f),
                "Direction should change when rotating vertically");
        }

        @Test
        @DisplayName("Should not update rotation when both delta values are zero")
        void shouldNotUpdateRotationWhenDeltasAreZero() {
            GamePosition initialDirection = player.getDirection();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                false, false, false, false, false,
                0f, 0f,
                0.016f
            );

            interactor.execute(inputData);

            GamePosition newDirection = player.getDirection();
            assertTrue(initialDirection.epsilonEquals(newDirection, 0.001f),
                "Direction should remain unchanged when no rotation input");
        }
    }

    @Nested
    @DisplayName("Ground Movement Tests")
    class GroundMovementTests {

        @BeforeEach
        void setUpGroundMovement() {
            // Remove player from any ride
            player.setCurrentRide(null);
        }

        @Test
        @DisplayName("Should move forward when forward is pressed")
        void shouldMoveForwardWhenForwardPressed() {
            GamePosition initialPosition = player.getPosition();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                true, false, false, false, false,
                0f, 0f,
                1.0f // 1 second delta time for easier calculation
            );

            interactor.execute(inputData);

            GamePosition newPosition = player.getPosition();
            assertFalse(initialPosition.epsilonEquals(newPosition, 0.001f),
                "Position should change when moving forward");
        }

        @Test
        @DisplayName("Should move backward when backward is pressed")
        void shouldMoveBackwardWhenBackwardPressed() {
            GamePosition initialPosition = player.getPosition();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                false, true, false, false, false,
                0f, 0f,
                1.0f
            );

            interactor.execute(inputData);

            GamePosition newPosition = player.getPosition();
            assertFalse(initialPosition.epsilonEquals(newPosition, 0.001f),
                "Position should change when moving backward");
        }

        @Test
        @DisplayName("Forward and backward should oppose each other")
        void forwardAndBackwardShouldCancel() {
            GamePosition initialPosition = player.getPosition();

            // Press both forward and backward
            PlayerMovementInputData inputData = new PlayerMovementInputData(
                true, true, false, false, false,
                0f, 0f,
                1.0f
            );

            interactor.execute(inputData);

            GamePosition newPosition = player.getPosition();
            // Movements should roughly cancel out
            assertTrue(initialPosition.epsilonEquals(newPosition, 0.01f),
                "Position should remain roughly the same when forward and backward are both pressed");
        }

        @Test
        @DisplayName("Should move left when left is pressed")
        void shouldMoveLeftWhenLeftPressed() {
            GamePosition initialPosition = player.getPosition();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                false, false, true, false, false,
                0f, 0f,
                1.0f
            );

            interactor.execute(inputData);

            GamePosition newPosition = player.getPosition();
            assertFalse(initialPosition.epsilonEquals(newPosition, 0.001f),
                "Position should change when moving left");
        }

        @Test
        @DisplayName("Should move right when right is pressed")
        void shouldMoveRightWhenRightPressed() {
            GamePosition initialPosition = player.getPosition();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                false, false, false, true, false,
                0f, 0f,
                1.0f
            );

            interactor.execute(inputData);

            GamePosition newPosition = player.getPosition();
            assertFalse(initialPosition.epsilonEquals(newPosition, 0.001f),
                "Position should change when moving right");
        }

        @Test
        @DisplayName("Should not move when no movement keys are pressed")
        void shouldNotMoveWhenNoKeysPressed() {
            GamePosition initialPosition = player.getPosition();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                false, false, false, false, false,
                0f, 0f,
                1.0f
            );

            interactor.execute(inputData);

            GamePosition newPosition = player.getPosition();
            assertTrue(initialPosition.epsilonEquals(newPosition, 0.001f),
                "Position should remain unchanged when no keys are pressed");
        }

        @Test
        @DisplayName("Left and right should oppose each other")
        void leftAndRightShouldCancel() {
            GamePosition initialPosition = player.getPosition();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                false, false, true, true, false,
                0f, 0f,
                1.0f
            );

            interactor.execute(inputData);

            GamePosition newPosition = player.getPosition();
            // Movements should roughly cancel out
            assertTrue(initialPosition.epsilonEquals(newPosition, 0.01f),
                "Position should remain roughly the same when left and right are both pressed");
        }
    }

    @Nested
    @DisplayName("Sprint Mode Tests")
    class SprintModeTests {

        @BeforeEach
        void setUpGroundMovement() {
            player.setCurrentRide(null);
        }

        @Test
        @DisplayName("Should move faster when sprinting")
        void shouldMoveFasterWhenSprinting() {
            GamePosition initialPosition = player.getPosition();

            // Move forward without sprinting
            PlayerMovementInputData walkInput = new PlayerMovementInputData(
                true, false, false, false, false,
                0f, 0f,
                1.0f
            );
            interactor.execute(walkInput);
            GamePosition walkPosition = player.getPosition();
            float walkDistance = walkPosition.dst(initialPosition);

            // Reset player position
            player = new Player(new GamePosition(0, 0, 0));
            interactor = new PlayerMovementInteractor(player);
            player.setCurrentRide(null);

            // Move forward with sprinting
            PlayerMovementInputData sprintInput = new PlayerMovementInputData(
                true, false, false, false, true, // sprinting = true
                0f, 0f,
                1.0f
            );
            interactor.execute(sprintInput);
            GamePosition sprintPosition = player.getPosition();
            float sprintDistance = sprintPosition.dst(new GamePosition(0, 0, 0));

            assertTrue(sprintDistance > walkDistance,
                "Sprint distance (" + sprintDistance + ") should be greater than walk distance (" + walkDistance + ")");
        }

        @Test
        @DisplayName("Sprint multiplier should be 5x")
        void sprintShouldBe5xFaster() {
            // Reset player
            player = new Player(new GamePosition(0, 0, 0));
            interactor = new PlayerMovementInteractor(player);
            player.setCurrentRide(null);
            GamePosition startPos = player.getPosition();

            // Move forward without sprinting
            PlayerMovementInputData walkInput = new PlayerMovementInputData(
                true, false, false, false, false,
                0f, 0f,
                1.0f
            );
            interactor.execute(walkInput);
            float walkDistance = player.getPosition().dst(startPos);

            // Reset player
            player = new Player(new GamePosition(0, 0, 0));
            interactor = new PlayerMovementInteractor(player);
            player.setCurrentRide(null);
            startPos = player.getPosition();

            // Move forward with sprinting
            PlayerMovementInputData sprintInput = new PlayerMovementInputData(
                true, false, false, false, true,
                0f, 0f,
                1.0f
            );
            interactor.execute(sprintInput);
            float sprintDistance = player.getPosition().dst(startPos);

            // Sprint should be exactly 5x
            assertEquals(5.0f, sprintDistance / walkDistance, 0.01f,
                "Sprint speed should be 5x walking speed");
        }
    }

    @Nested
    @DisplayName("Train Movement Tests")
    class TrainMovementTests {

        private Train train;

        @BeforeEach
        void setUpTrainMovement() {
            train = new Train(-1, new GamePosition(0, 0, 0));
            train.setCurrentFuel(5000);
            player.setCurrentRide(train);
        }

        @Test
        @DisplayName("Should not move on train when train has no fuel")
        void shouldNotMoveOnTrainWhenNoFuel() {
            train.setCurrentFuel(0);

            interactor.execute(new PlayerMovementInputData(
                true, false, false, false, false,
                0f, 0f,
                1.0f
            ));

            float trainThrottle = train.getThrottle();
            assertEquals(0, trainThrottle, "Position should remain unchanged when train has no fuel");
        }

        @Test
        @DisplayName("Should not move on train when forward is not pressed")
        void shouldNotMoveOnTrainWhenForwardNotPressed() {

            interactor.execute(new PlayerMovementInputData(
                false, true, true, true, false, // backward, left, right but not forward
                0f, 0f,
                1.0f
            ));

            float trainThrottle = train.getThrottle();
            assertEquals(trainThrottle, 0,
                "Position should remain unchanged on train when forward is not pressed");
        }

        @Test
        @DisplayName("Train throttle should increase")
        void trainThrottleIncreaseOnForwardPress() {
            train.setSpeed(30);
            PlayerMovementInputData inputData = new PlayerMovementInputData(
                true, false, false, false, false,
                0f, 0f,
                1.0f
            );

            interactor.execute(inputData);

            float trainThrottle = train.getThrottle();

            assertTrue(trainThrottle > 0,
                "Movement distance should match train speed");
        }

        @Test
        @DisplayName("Train fuel should decrease")
        void trainFuelDecreasesOnMove() {
            train.setSpeed(30);
            float previousFuel = train.getCurrentFuel();
            PlayerMovementInputData inputData = new PlayerMovementInputData(
                true, false, false, false, false,
                0f, 0f,
                1.0f
            );

            interactor.execute(inputData);

            float trainFuel = train.getCurrentFuel();

            assertTrue(trainFuel - previousFuel < 0,
                "Movement distance should match train speed");
        }

        @Test
        @DisplayName("Sprint should not affect train movement")
        void sprintShouldNotAffectTrainMovement() {
            train.setSpeed(30);

            PlayerMovementInputData noSprintInput = new PlayerMovementInputData(
                true, false, false, false, false,
                0f, 0f,
                1.0f
            );
            interactor.execute(noSprintInput);
            float throttle1 = train.getThrottle();

            // Reset player position
            player = new Player(new GamePosition(0, 0, 0));
            train = new Train(-1, new GamePosition(0, 0, 0));
            train.setCurrentFuel(50);
            train.setSpeed(30);
            player.setCurrentRide(train);
            interactor = new PlayerMovementInteractor(player);

            // Move with sprint
            PlayerMovementInputData sprintInput = new PlayerMovementInputData(
                true, false, false, false, true, // sprinting = true
                0f, 0f,
                1.0f
            );
            interactor.execute(sprintInput);
            float throttle2 = train.getThrottle();

            assertEquals(throttle1, throttle2, 0.01f,
                "Sprint should not affect train movement speed");
        }
    }

    @Nested
    @DisplayName("Delta Time Tests")
    class DeltaTimeTests {

        @BeforeEach
        void setUp() {
            player.setCurrentRide(null);
        }

        @Test
        @DisplayName("Larger delta time should result in more movement")
        void largerDeltaTimeShouldResultInMoreMovement() {
            // Move with small delta time
            GamePosition start1 = player.getPosition();
            PlayerMovementInputData smallDeltaInput = new PlayerMovementInputData(
                true, false, false, false, false,
                0f, 0f,
                0.016f // ~60 FPS
            );
            interactor.execute(smallDeltaInput);
            float distance1 = player.getPosition().dst(start1);

            // Reset
            player = new Player(new GamePosition(0, 0, 0));
            player.setCurrentRide(null);
            interactor = new PlayerMovementInteractor(player);

            // Move with larger delta time
            GamePosition start2 = player.getPosition();
            PlayerMovementInputData largeDeltaInput = new PlayerMovementInputData(
                true, false, false, false, false,
                0f, 0f,
                0.1f // larger delta time
            );
            interactor.execute(largeDeltaInput);
            float distance2 = player.getPosition().dst(start2);

            assertTrue(distance2 > distance1,
                "Larger delta time should result in more movement");
        }

        @Test
        @DisplayName("Zero delta time should result in no movement")
        void zeroDeltaTimeShouldResultInNoMovement() {
            GamePosition initialPosition = player.getPosition();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                true, false, false, false, false,
                0f, 0f,
                0f // zero delta time
            );

            interactor.execute(inputData);

            GamePosition newPosition = player.getPosition();
            assertTrue(initialPosition.epsilonEquals(newPosition, 0.001f),
                "Zero delta time should result in no movement");
        }
    }

    @Nested
    @DisplayName("Combined Movement Tests")
    class CombinedMovementTests {

        @BeforeEach
        void setUp() {
            player.setCurrentRide(null);
        }

        @Test
        @DisplayName("Should support diagonal movement (forward + right)")
        void shouldSupportDiagonalMovement() {
            GamePosition initialPosition = player.getPosition();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                true, false, false, true, false, // forward + right
                0f, 0f,
                1.0f
            );

            interactor.execute(inputData);

            GamePosition newPosition = player.getPosition();
            assertFalse(initialPosition.epsilonEquals(newPosition, 0.001f),
                "Position should change with diagonal movement");
        }

        @Test
        @DisplayName("Should allow rotation while moving")
        void shouldAllowRotationWhileMoving() {
            GamePosition initialDirection = player.getDirection();
            GamePosition initialPosition = player.getPosition();

            PlayerMovementInputData inputData = new PlayerMovementInputData(
                true, false, false, false, false,
                10f, 5f, // rotation input
                1.0f
            );

            interactor.execute(inputData);

            // Both direction and position should change
            assertFalse(initialDirection.epsilonEquals(player.getDirection(), 0.001f),
                "Direction should change");
            assertFalse(initialPosition.epsilonEquals(player.getPosition(), 0.001f),
                "Position should change");
        }
    }
}
