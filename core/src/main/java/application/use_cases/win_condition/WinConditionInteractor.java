package application.use_cases.win_condition;

import com.badlogic.gdx.Gdx;
import domain.World;
import domain.entities.Entity;
import data_access.mockLogic.EntityStorage;
import domain.entities.EntityType;
import java.util.Optional;

/**
 * Checks if the primary TRAIN entity has reached or passed the world's end coordinate (X-axis).
 * If the condition is met, it triggers the game exit.
 */
public class WinConditionInteractor implements WinConditionInputBoundary {

    private final World world;
    private final EntityStorage entityStorage;
    private boolean isGameOver = false;

    public WinConditionInteractor(World world, EntityStorage entityStorage) {
        this.world = world;
        this.entityStorage = entityStorage;
    }

    /**
     * Finds the first entity identified as a TRAIN (based on type).
     * NOTE: This assumes there is one main train or that the first one found is the correct one.
     */
    private Optional<Entity> findTrain() {
        // You would typically iterate through entityStorage.getAll()
        // and check if entity.getType() == EntityType.TRAIN

        // Mock implementation: Find the first entity that is the train
        return entityStorage.getAll().stream()
            .filter(entity -> entity.getType() == EntityType.TRAIN)
            .findFirst();
    }

    @Override
    public WinConditionOutputData execute() {
        if (isGameOver) {
            // Prevent repeated execution if the win state is already reached
            return new WinConditionOutputData(true, "Congratulations! You have conquered the Zombie Express!");
        }

        Optional<Entity> train = findTrain();

        if (train.isPresent()) {
            float trainX = train.get().getPosition().x;
            float worldEndX = world.getWorldEndCoordinateX();

            if (trainX >= worldEndX) {
                isGameOver = true;
                String message = "Congratulations! You have conquered the Zombie Express!";

                System.out.println("--- GAME WON: " + message + " ---");
                Gdx.app.exit(); // Initiate game exit

                return new WinConditionOutputData(true, message);
            }
        }

        return new WinConditionOutputData(false, "");
    }
}
