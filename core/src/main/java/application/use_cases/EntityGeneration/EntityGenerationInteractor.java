package application.use_cases.EntityGeneration;

import com.badlogic.gdx.math.Vector3;
import domain.entities.ZombieStorage;
import domain.entities.Zombie;

public class EntityGenerationInteractor implements EntityGenerationInputBoundary{
    // Populates zombieStorage

    private ZombieStorage zombieStorage;

    public EntityGenerationInteractor(ZombieStorage zombieStorage) {
        this.zombieStorage = zombieStorage;
    }

    @Override
    public void execute(EntityGenerationInputData inputData) {
        // generate a new zombie at position 0,0,0
        Vector3 position = new Vector3(0f, 0f, 0f);
        Zombie zombie = new Zombie(position);
        //zombie.setRendered(true);
        zombieStorage.addZombie(zombie);

        }
    }

