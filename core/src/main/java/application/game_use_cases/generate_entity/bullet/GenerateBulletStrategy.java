package application.game_use_cases.generate_entity.bullet;

import application.game_use_cases.generate_entity.GenerateEntityInputData;
import application.game_use_cases.generate_entity.GenerateEntityStrategy;
import domain.entities.Bullet;
import domain.entities.Entity;

public class GenerateBulletStrategy implements GenerateEntityStrategy {
    @Override
    public Entity execute(GenerateEntityInputData inputData) {
        return new Bullet(inputData.getId(), inputData.getPosition(), true);
    }
}
