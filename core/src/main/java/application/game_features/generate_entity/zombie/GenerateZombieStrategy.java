package application.game_features.generate_entity.zombie;

import application.game_features.generate_entity.GenerateEntityInputData;
import application.game_features.generate_entity.GenerateEntityStrategy;
import domain.entities.Entity;
import domain.entities.Zombie;

public class GenerateZombieStrategy implements GenerateEntityStrategy {
    @Override
    public Entity execute(GenerateEntityInputData inputData) {
        return new Zombie(inputData.getId(), inputData.getPosition(), true);
    }
}
