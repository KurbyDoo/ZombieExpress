package application.game_use_cases.generate_entity.pickup;

import application.game_use_cases.generate_entity.GenerateEntityStrategy;
import application.game_use_cases.generate_entity.GenerateEntityInputData;
import domain.entities.Entity;
import domain.entities.PickupEntity;

public class GeneratePickupStrategy implements GenerateEntityStrategy {

    @Override
    public Entity execute(GenerateEntityInputData genericInput) {
        GeneratePickupInputData input = (GeneratePickupInputData) genericInput;

        return new PickupEntity(
            input.getId(),
            input.getItem(),
            input.getPosition(),
            true
        );
    }
}
