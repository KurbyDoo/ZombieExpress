package application.game_use_cases.generate_entity.player_entity;

import application.game_use_cases.generate_entity.GenerateEntityInputData;
import application.game_use_cases.generate_entity.GenerateEntityStrategy;
import domain.entities.Entity;
import domain.entities.PlayerEntity;

public class GeneratePlayerEntityStrategy implements GenerateEntityStrategy {

    @Override
    public Entity execute(GenerateEntityInputData inputData){
        return new PlayerEntity(inputData.getId(), inputData.getPosition());
    }
}
