package application.use_cases.generate_entity.zombie;

import application.use_cases.generate_entity.GenerateEntityInputData;
import com.badlogic.gdx.math.Vector3;
import domain.Chunk;
import domain.entities.EntityType;

public class GenerateZombieInputData extends GenerateEntityInputData {
    public GenerateZombieInputData(Vector3 position, Chunk chunk) {
        super(EntityType.ZOMBIE, position, chunk);
    }
}
