package application.game_use_cases.generate_entity.train;

import domain.GamePosition;
import application.game_use_cases.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;

public class GenerateTrainInputData extends GenerateEntityInputData {
    public GenerateTrainInputData(GamePosition position) {
        super(EntityType.TRAIN, position);
    }
}
