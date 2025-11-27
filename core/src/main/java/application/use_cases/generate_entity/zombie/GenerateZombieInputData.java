package application.use_cases.generate_entity.zombie;

import domain.GamePosition;
import application.use_cases.generate_entity.GenerateEntityInputData;
import domain.Chunk;
import domain.entities.EntityType;

public class GenerateZombieInputData extends GenerateEntityInputData {
    public GenerateZombieInputData(GamePosition position, Chunk chunk) {
        super(EntityType.ZOMBIE, position, chunk);
    }
}
