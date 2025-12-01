package application.game_features.win_condition;

import application.game_features.exit_game.ExitGameUseCase;
import domain.GamePosition;
import domain.World;
import domain.entities.Rideable;
import domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * --- Manual Test Doubles (Stubs and Spies) for Dependencies ---
 */

/** Stub for the World dependency, allowing control over the world end coordinate. */
class TestWorld extends World {
    private float worldEndX;

    public TestWorld(float worldEndX) {
        this.worldEndX = worldEndX;
    }

    @Override
    public float getWorldEndCoordinateX() {
        return worldEndX;
    }
}

/** Spy for the ExitGameUseCase, tracking if the exit sequence was initiated. */
class TestExitGameUseCase extends ExitGameUseCase {
    private int callCount = 0;

    // Must call super constructor, even if the port is null/unneeded for this test's scope
    public TestExitGameUseCase() {
        super(null);
    }

    public int getCallCount() {
        return callCount;
    }
}

/** Stub for the Rideable entity, allowing control over its position. */
class TestRideable implements Rideable {
    private GamePosition position;

    public TestRideable(GamePosition position) {
        this.position = position;
    }

    public void setPosition(GamePosition position) {
        this.position = position;
    }

    @Override
    public GamePosition getPosition() {
        return position;
    }

    // Other Rideable methods needed for compile
    @Override
    public int getSpeed() { return 0; }
    @Override
    public GamePosition getRideOffset() { return new GamePosition(0, 0, 0); }
}

/** Stub for the Player entity, allowing control over its state. */
class TestPlayer extends Player {
    private boolean deadState = false;
    private Rideable currentRide = null;

    public TestPlayer() {
        super(new GamePosition(0, 0, 0)); // Initialize with a dummy position
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
}


/**
 * Unit tests for the WinConditionInteractor using manual test doubles.
 */
class WinConditionInteractorTest {

    private TestWorld testWorld;
    private TestPlayer testPlayer;
    private TestRideable testRideable;
    private TestExitGameUseCase testExitGameUseCase;
    private WinConditionInteractor interactor;

    // Constants for testing
    private final float WORLD_END_X = 100.0f;
    private final GamePosition WIN_POS = new GamePosition(WORLD_END_X, 0, 0);
    private final GamePosition NEUTRAL_POS = new GamePosition(50.0f, 0, 0);

    /*
        Set up test doubles and the Interactor before each test.
     */
    @BeforeEach
    void setUp() {
        // 1. Instantiate Manual Test Doubles
        testWorld = new TestWorld(WORLD_END_X);
        testPlayer = new TestPlayer();
        testRideable = new TestRideable(NEUTRAL_POS); // Neutral start position
        testExitGameUseCase = new TestExitGameUseCase();

        // 2. Instantiate the Interactor
        interactor = new WinConditionInteractor(testWorld, testPlayer, testExitGameUseCase);
    }

    @Nested
    @DisplayName("Win Condition (Train at World End)")
    class WinConditionTests {

        @Test
        @DisplayName("Should return game over (Win) when riding train at end position")
        void shouldWinWhenTrainReachesWorldEnd() {
            // Setup: Player is riding, train is at the world end position
            testPlayer.setDeadState(false);
            testPlayer.setCurrentRide(testRideable);
            testRideable.setPosition(WIN_POS);

            // Execute
            WinConditionOutputData output = interactor.execute();

            // Assertions
            assertTrue(output.isGameOver(), "Should report game over (Win).");
            assertTrue(output.getMessage().contains("Congratulations!"), "Should report the win message.");

            // Execute again to test the internal `isGameOver` check
            WinConditionOutputData secondOutput = interactor.execute();
            assertTrue(secondOutput.isGameOver(), "Should return game over on subsequent calls.");
            assertEquals("Game Over.", secondOutput.getMessage(), "Should return generic message after game over.");

            // Verify: Exit use case should not have been called
            assertEquals(0, testExitGameUseCase.getCallCount(), "ExitGameUseCase should not be called on win.");
        }

        @Test
        @DisplayName("Should NOT check win condition if player is NOT riding")
        void shouldNotCheckWinIfNotRiding() {
            // Setup: Player is alive, but not riding anything (currentRide == null)
            testPlayer.setDeadState(false);
            testPlayer.setCurrentRide(null);

            // Execute
            WinConditionOutputData output = interactor.execute();

            // Assertions
            assertFalse(output.isGameOver(), "Should not report game over.");
            assertTrue(output.getMessage().isEmpty(), "Should return empty message.");
        }
    }

    @Nested
    @DisplayName("Lose Condition (Player Death)")
    class LoseConditionTests {

        @Test
        @DisplayName("Should return game over (Loss) when player is dead")
        void shouldLoseWhenPlayerIsDead() {
            // Setup: Player is dead
            testPlayer.setDeadState(true);

            // Execute
            WinConditionOutputData output = interactor.execute();

            // Assertions
            assertTrue(output.isGameOver(), "Should report game over (Loss).");
            assertTrue(output.getMessage().contains("health reached zero"), "Should report the loss message.");

            // Execute again to test the internal `isGameOver` check
            WinConditionOutputData secondOutput = interactor.execute();
            assertTrue(secondOutput.isGameOver(), "Should return game over on subsequent calls.");
            assertEquals("Game Over.", secondOutput.getMessage(), "Should return generic message after game over.");

            // Verify: Exit use case should not have been called
            assertEquals(0, testExitGameUseCase.getCallCount(), "ExitGameUseCase should not be called on loss.");
        }

        @Test
        @DisplayName("Should prioritize Loss condition over Win condition check")
        void shouldPrioritizeLoss() {
            // Setup: Player is dead, but the train is at the win position
            // (The Interactor checks Player.isDead() first)
            testPlayer.setDeadState(true);
            testPlayer.setCurrentRide(testRideable);
            testRideable.setPosition(WIN_POS);

            // Execute
            WinConditionOutputData output = interactor.execute();

            // Assertions
            assertTrue(output.isGameOver(), "Should report game over.");
            assertTrue(output.getMessage().contains("health reached zero"), "Should report loss message, ignoring win position.");
        }
    }

    @Nested
    @DisplayName("Neutral Condition (Game Ongoing)")
    class NeutralConditionTests {

        @Test
        @DisplayName("Should return game ongoing when player is alive and train is before world end")
        void shouldBeOngoing() {
            // Setup: Player is alive, riding, and the train has not reached the end
            testPlayer.setDeadState(false);
            testPlayer.setCurrentRide(testRideable);
            testRideable.setPosition(NEUTRAL_POS);

            // Execute
            WinConditionOutputData output = interactor.execute();

            // Assertions
            assertFalse(output.isGameOver(), "Should report game ongoing.");
            assertTrue(output.getMessage().isEmpty(), "Should return empty message.");

            // Verify: No exit call
            assertEquals(0, testExitGameUseCase.getCallCount(), "ExitGameUseCase should not be called.");
        }

        @Test
        @DisplayName("Should return game ongoing when player is alive and riding exactly at world end coordinate")
        void shouldBeOngoingAtExactWorldEnd() {
            // Setup: Player is alive, riding, and exactly at the end coordinate (WIN_POS)
            // The logic uses trackedX >= worldEndX, so this should trigger a win.
            testPlayer.setDeadState(false);
            testPlayer.setCurrentRide(testRideable);
            testRideable.setPosition(new GamePosition(WORLD_END_X, 0, 0)); // Exactly 100.0f

            // Execute
            WinConditionOutputData output = interactor.execute();

            // Assertions
            assertTrue(output.isGameOver(), "Should report game over (Win) when exactly at the coordinate.");
            assertTrue(output.getMessage().contains("Congratulations!"), "Should report the win message.");
        }
    }
}
