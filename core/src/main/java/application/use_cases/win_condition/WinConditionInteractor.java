package application.use_cases.win_condition;

import com.badlogic.gdx.Gdx;
import domain.World;
import domain.player.Player;
import com.badlogic.gdx.math.Vector3;

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

    public WinConditionInteractor(World world, Player player) {
        this.world = world;
        this.player = player;
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
            Gdx.app.exit(); // Triggers application exit

            return new WinConditionOutputData(true, message);
        }

        // --- CASE A: CHECK WIN CONDITION (TRAIN AT WORLD END) ---
        // Player.getTrackedPosition() correctly returns the Ridable's position (the train)
        Vector3 trackedPosition = player.getPosition();

        float trackedX = trackedPosition.x;
        float worldEndX = world.getWorldEndCoordinateX();

        if (trackedX >= worldEndX) {
            isGameOver = true;
            String message = "Congratulations! You conquered the Zombie Express! Final distance: " + (int)trackedX;

            System.out.println("--- GAME WON: " + message + " ---");

            return new WinConditionOutputData(true, message);
        }
        // Return default state if no end condition is met
        return new WinConditionOutputData(false, "");
    }
}
