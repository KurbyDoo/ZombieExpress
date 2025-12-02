package application.game_features.generate_entity;

import domain.entities.Entity;

public interface GenerateEntityStrategy {
    // Used for Factory - can be deleted later
    // Need stronger typing
    Entity execute(GenerateEntityInputData inputData);
}
