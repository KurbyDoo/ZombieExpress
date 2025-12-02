package application.game_features.generate_entity.bullet;

import application.game_features.generate_entity.GenerateEntityInputData;
import application.game_features.generate_entity.GenerateEntityStrategy;
import domain.entities.Bullet;
import domain.entities.Entity;

public class GenerateBulletStrategy implements GenerateEntityStrategy {
    @Override
    public Entity execute(GenerateEntityInputData genericInput) {
        GenerateBulletInputData input = (GenerateBulletInputData) genericInput;

        return new Bullet(
            input.getId(),
            input.getPosition(),
            input.getDirection(),
            true
        );
    }
}
