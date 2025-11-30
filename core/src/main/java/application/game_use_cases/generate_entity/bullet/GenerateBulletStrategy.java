package application.game_use_cases.generate_entity.bullet;

import application.game_use_cases.generate_entity.GenerateEntityInputData;
import application.game_use_cases.generate_entity.GenerateEntityStrategy;
import application.game_use_cases.generate_entity.pickup.GeneratePickupInputData;
import domain.entities.Bullet;
import domain.entities.Entity;
import domain.entities.PickupEntity;

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
