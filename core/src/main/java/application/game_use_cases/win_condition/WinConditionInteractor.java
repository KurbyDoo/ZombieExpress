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

    private boolean isGameOver = false;

    public WinConditionInteractor(World world, Player player, ExitGameUseCase exitGameUseCase) {
        this.world = world;
        this.player = player;
    }

    @Override
    public WinConditionOutputData execute() {
        if (isGameOver) {
            return new WinConditionOutputData(true, "Game Over.", player.getScore());
        }

        // --- CASE B: CHECK LOSE CONDITION (PLAYER DEATH) ---
        if (player.isDead()) {
            isGameOver = true;
            String message = "Game Over! You died. You succumbed to the Zombie Express.";

            System.out.println("--- GAME LOST: Player Died ---");

            int score = player.getScore();
            System.out.println("[WinCondition] GameOver=" + isGameOver
                + "  Message=" + message
                + "  Score=" + score);
            return new WinConditionOutputData(true, message,  player.getScore());
        }

        // --- CASE A: CHECK WIN CONDITION (TRAIN AT WORLD END) ---

        Rideable currentRide = player.getCurrentRide();

        // The win condition only applies if the player is actively riding the train
        if (currentRide != null) {

            GamePosition trackedPosition = currentRide.getPosition();

            float trackedX = trackedPosition.x;
            float worldEndX = world.getWorldEndCoordinateX();

            if (trackedX >= worldEndX) {

                isGameOver = true;
                String message = "Congratulations! You conquered the Zombie Express! Final distance: " + (int)trackedX;

                System.out.println("--- GAME WON: " + message + " ---");

                int score = player.getScore();
                System.out.println("[WinCondition] GameOver=" + isGameOver
                    + "  Message=" + message
                    + "  Score=" + score);

                return new WinConditionOutputData(true, message,  player.getScore());
            }
        }

        // Return default state if no end condition is met
        return new WinConditionOutputData(false, "", player.getScore());
    }
}
