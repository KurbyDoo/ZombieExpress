package application.use_cases.generate_entity.pickup;

import application.use_cases.generate_entity.GenerateEntityInputData;
import application.use_cases.generate_entity.GenerateEntityStrategy;
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
