package application.use_cases.generate_entity.train;

import domain.GamePosition;
import application.use_cases.generate_entity.GenerateEntityInputData;
import domain.Chunk;
import domain.entities.EntityType;

public class GenerateTrainInputData extends GenerateEntityInputData {
    public GenerateTrainInputData(GamePosition position, Chunk chunk) {
        super(EntityType.TRAIN, position, chunk);
    }
}
