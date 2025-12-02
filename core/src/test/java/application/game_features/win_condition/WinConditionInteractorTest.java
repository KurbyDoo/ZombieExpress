package application.game_features.win_condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import application.game_features.exit_game.ExitGameUseCase;
import domain.entities.Rideable;
import domain.entities.Train;
import domain.player.Player;
import domain.world.GamePosition;
import domain.world.World;
import framework.data_access.IdToEntityStorage;

/**
 * --- Manual Test Doubles (Stubs) for Dependencies ---
 */

/**
 * Stub for the World dependency, allowing control over the world end coordinate.
 */
class TestWorld extends World {
    private final float worldEndX;

    public TestWorld(float worldEndX) {
        this.worldEndX = worldEndX;
    }

    @Override
    public float getWorldEndCoordinateX() {
        return worldEndX;
    }
}

/**
 * Stub for the Rideable entity, allowing control over its position.
 */
class TestRideable implements Rideable {
    private GamePosition position;

    public TestRideable(GamePosition position) {
        this.position = position;
    }

    @Override
    public GamePosition getPosition() {
        return position;
    }

    public void setPosition(GamePosition position) {
        this.position = position;
    }

    @Override
    public int getSpeed() {
        return 0;
    }

    @Override
    public GamePosition getRideOffset() {
        return new GamePosition(0, 0, 0);
    }
}

/**
 * Stub for the Player entity, allowing control over its state and score.
 */
class TestPlayer extends Player {
    private boolean deadState = false;
    private Rideable currentRide = null;
    private int score = 0;

    public TestPlayer() {
        super(new GamePosition(0, 0, 0)); // dummy position
    }

    @Override
    public boolean isDead() {
        return deadState;
    }

    public void setDeadState(boolean deadState) {
        this.deadState = deadState;
    }

    @Override
    public Rideable getCurrentRide() {
        return currentRide;
    }

    public void setCurrentRide(Rideable rideable) {
        this.currentRide = rideable;
    }

    @Override
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

/**
 * Test double for IdToEntityStorage.
 * Only implements what WinConditionInteractor needs: providing a Train for stopTrain().
 */
class TestIdToEntityStorage extends IdToEntityStorage {

    private Train train;

    public TestIdToEntityStorage() {
        super(new TestWorld(100)); // adjust/remove if your real IdToEntityStorage has a different constructor
    }

    @Override
    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }
}

/**
 * Unit tests for the WinConditionInteractor using manual test doubles.
 */
class WinConditionInteractorTest {

    // Constants for testing
    private static final float WORLD_END_X = 100.0f;
    private final GamePosition WIN_POS = new GamePosition(WORLD_END_X, 0, 0);
    private final GamePosition NEUTRAL_POS = new GamePosition(50.0f, 0, 0);
    private TestWorld testWorld;
    private TestPlayer testPlayer;
    private TestRideable testRideable;
    private TestIdToEntityStorage testEntityStorage;
    private Train testTrain;
    private WinConditionInteractor interactor;

    @BeforeEach
    void setUp() {
        testWorld = new TestWorld(WORLD_END_X);
        testPlayer = new TestPlayer();
        testRideable = new TestRideable(NEUTRAL_POS);

        // Real Train instance so we can assert its speed/throttle after stopTrain()
        testTrain = new Train(1, new GamePosition(0, 0, 0));
        testTrain.setSpeed(30);
        testTrain.setThrottle(1.5f);

        // Our IdToEntityStorage test double that always returns this train
        testEntityStorage = new TestIdToEntityStorage();
        testEntityStorage.setTrain(testTrain);

        // ExitGameUseCase is currently unused inside the interactor, so we can pass null
        ExitGameUseCase exitGameUseCase = null;

        interactor = new WinConditionInteractor(testWorld, testPlayer, testEntityStorage, exitGameUseCase);
    }

    @Nested
    @DisplayName("Win Condition (Train at World End)")
    class WinConditionTests {

        @Test
        @DisplayName("Should return game over (Win) when riding train at end position and stop the train")
        void shouldWinWhenTrainReachesWorldEnd_andStopTrain() {
            testPlayer.setDeadState(false);
            testPlayer.setCurrentRide(testRideable);
            testPlayer.setScore(123);
            testRideable.setPosition(WIN_POS);

            // Pre-check: speed/throttle non-zero
            assertTrue(testTrain.getSpeed() > 0, "Precondition: train speed should be > 0 before win.");
            // If you don't have getThrottle(), you can remove this assertion and only rely on setters.
            // Assume getThrottle exists; otherwise comment this out in your code.
            // assertTrue(testTrain.getThrottle() > 0, "Precondition: train throttle should be > 0 before win.");

            WinConditionOutputData output = interactor.execute();

            assertTrue(output.isGameOver(), "Should report game over (Win).");
            assertTrue(output.getMessage().contains("Congratulations!"),
                "Should report the win message.");
            assertEquals(123, output.getScore(), "Should propagate player's score.");
            // Train should have been stopped via IdToEntityStorage
            assertEquals(0, testTrain.getSpeed(), "Train speed should be set to 0 on win.");
            // If there is a getter for throttle, assert that as well
            // assertEquals(0f, testTrain.getThrottle(), "Train throttle should be set to 0 on win.");

            // Second call after game is already over
            WinConditionOutputData secondOutput = interactor.execute();
            assertTrue(secondOutput.isGameOver(), "Subsequent calls should still be game over.");
            assertEquals("Game Over.", secondOutput.getMessage(), "Should return generic message after game over.");
        }

        @Test
        @DisplayName("Should NOT check win condition if player is NOT riding")
        void shouldNotCheckWinIfNotRiding() {
            testPlayer.setDeadState(false);
            testPlayer.setCurrentRide(null);
            testPlayer.setScore(10);

            WinConditionOutputData output = interactor.execute();

            assertFalse(output.isGameOver(), "Should not report game over.");
            assertTrue(output.getMessage().isEmpty(), "Message should be empty when no condition is met.");
            assertEquals(10, output.getScore(), "Score should be passed through unchanged.");
        }
    }

    @Nested
    @DisplayName("Lose Condition (Player Death)")
    class LoseConditionTests {

        @Test
        @DisplayName("Should return game over (Loss) when player is dead")
        void shouldLoseWhenPlayerIsDead() {
            testPlayer.setDeadState(true);
            testPlayer.setScore(999);

            WinConditionOutputData output = interactor.execute();

            assertTrue(output.isGameOver(), "Should report game over (Loss).");
            assertTrue(output.getMessage().contains("You died"),
                "Message should mention that the player died.");
            assertTrue(output.getMessage().contains("Zombie Express"),
                "Message should mention Zombie Express.");
            assertEquals(999, output.getScore(), "Score should come from player.");

            WinConditionOutputData secondOutput = interactor.execute();
            assertTrue(secondOutput.isGameOver(), "Subsequent calls should still be game over.");
            assertEquals("Game Over.", secondOutput.getMessage(),
                "Should return generic message after game over.");
        }

        @Test
        @DisplayName("Should prioritize Loss condition over Win condition check")
        void shouldPrioritizeLoss() {
            testPlayer.setDeadState(true);
            testPlayer.setCurrentRide(testRideable);
            testRideable.setPosition(WIN_POS);

            WinConditionOutputData output = interactor.execute();

            assertTrue(output.isGameOver(), "Should report game over.");
            assertTrue(output.getMessage().contains("You died"),
                "Loss condition should override win condition even if train is at world end.");
        }
    }

    @Nested
    @DisplayName("Neutral Condition (Game Ongoing)")
    class NeutralConditionTests {

        @Test
        @DisplayName("Should return game ongoing when player is alive and train is before world end")
        void shouldBeOngoing() {
            testPlayer.setDeadState(false);
            testPlayer.setCurrentRide(testRideable);
            testRideable.setPosition(NEUTRAL_POS);

            WinConditionOutputData output = interactor.execute();

            assertFalse(output.isGameOver(), "Should report game ongoing.");
            assertTrue(output.getMessage().isEmpty(), "Message should be empty when game continues.");
        }

        @Test
        @DisplayName("Should win when exactly at world end coordinate")
        void shouldWinAtExactWorldEnd() {
            testPlayer.setDeadState(false);
            testPlayer.setCurrentRide(testRideable);
            testRideable.setPosition(new GamePosition(WORLD_END_X, 0, 0));

            WinConditionOutputData output = interactor.execute();

            assertTrue(output.isGameOver(),
                "trackedX >= worldEndX means we should win at exact coordinate.");
            assertTrue(output.getMessage().contains("Congratulations!"),
                "Should report the win message.");
        }
    }
}
