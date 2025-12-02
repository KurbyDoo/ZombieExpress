package application.game_features.generate_entity.train;

import application.game_features.generate_entity.GenerateEntityInputData;
import application.game_features.generate_entity.GenerateEntityStrategy;
import domain.entities.Entity;
import domain.entities.Train;

public class GenerateTrainStrategy implements GenerateEntityStrategy {
    @Override
    public Entity execute(GenerateEntityInputData inputData) {
        // We set isVisible to true since the train needs to be rendered initially.
        // It's crucial to use the data passed via inputData to maintain the pattern.

        return new Train(inputData.getId(), inputData.getPosition());
    }
}
