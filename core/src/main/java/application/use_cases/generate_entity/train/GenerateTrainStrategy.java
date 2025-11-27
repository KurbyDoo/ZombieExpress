package application.use_cases.generate_entity.train;

import application.use_cases.generate_entity.GenerateEntityInputData;
import application.use_cases.generate_entity.GenerateEntityStrategy;
import domain.entities.Entity;
import domain.entities.EntityType;
import domain.entities.Train; // Import your specific Train class

public class GenerateTrainStrategy implements GenerateEntityStrategy {
    @Override
    public Entity execute(GenerateEntityInputData inputData) {
        // We set isVisible to true since the train needs to be rendered initially.
        // It's crucial to use the data passed via inputData to maintain the pattern.

        return new Train(inputData.getId(), inputData.getPosition());
    }
}
