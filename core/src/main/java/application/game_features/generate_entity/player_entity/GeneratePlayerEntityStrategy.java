package application.game_features.generate_entity.player_entity;

import application.game_features.generate_entity.GenerateEntityInputData;
import application.game_features.generate_entity.GenerateEntityStrategy;
import domain.entities.Entity;
import domain.entities.PlayerEntity;

public class GeneratePlayerEntityStrategy implements GenerateEntityStrategy {

    @Override
    public Entity execute(GenerateEntityInputData inputData) {
        return new PlayerEntity(inputData.getId(), inputData.getPosition());
    }
}
