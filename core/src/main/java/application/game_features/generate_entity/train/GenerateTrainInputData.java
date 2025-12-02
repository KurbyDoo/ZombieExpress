package application.game_features.generate_entity.train;

import application.game_features.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;
import domain.world.GamePosition;

public class GenerateTrainInputData extends GenerateEntityInputData {
    public GenerateTrainInputData(GamePosition position) {
        super(EntityType.TRAIN, position);
    }
}
