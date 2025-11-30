package application.game_use_cases.win_condition;

import application.game_use_cases.exit_game.ExitGameUseCase;
import domain.GamePosition;
import domain.World;
import domain.entities.Rideable;
import domain.player.Player;

/**
 * The Interactor responsible for checking all possible game-ending conditions:
 * 1. Lose Condition (Player health is zero).
 * 2. Win Condition (Train reaches the world end).
 * If a condition is met, it triggers the application exit and reports the result.
 */
public class WinConditionInteractor implements WinConditionInputBoundary {

    private final World world;
    private final Player player;
    private final ExitGameUseCase exitGameUseCase;

    private boolean isGameOver = false;

    public WinConditionInteractor(World world, Player player, ExitGameUseCase exitGameUseCase) {
        this.world = world;
        this.player = player;
        this.exitGameUseCase = exitGameUseCase;
    }

    @Override
    public WinConditionOutputData execute() {
        if (isGameOver) {
            return new WinConditionOutputData(true, "Game Over.");
        }

        // --- CASE B: CHECK LOSE CONDITION (PLAYER DEATH) ---
        if (player.isDead()) {
            isGameOver = true;
            String message = "Game Over! Your health reached zero. You succumbed to the Zombie Express.";

            System.out.println("--- GAME LOST: Player Died ---");
            return new WinConditionOutputData(true, message);
        }

        // --- CASE A: CHECK WIN CONDITION (TRAIN AT WORLD END) ---

        Rideable currentRide = player.getCurrentRide();

        // The win condition only applies if the player is actively riding the train
        if (currentRide != null) {

            GamePosition trackedPosition = currentRide.getPosition();

            float trackedX = trackedPosition.x;
            float worldEndX = world.getWorldEndCoordinateX();

            // if (trackedX >= worldEndX) {
            if (trackedX >= 20) {

                isGameOver = true;
                String message = "Congratulations! You conquered the Zombie Express! Final distance: " + (int)trackedX;

                System.out.println("--- GAME WON: " + message + " ---");

                return new WinConditionOutputData(true, message);
            }
        }

        // Return default state if no end condition is met
        return new WinConditionOutputData(false, "");
    }
}
