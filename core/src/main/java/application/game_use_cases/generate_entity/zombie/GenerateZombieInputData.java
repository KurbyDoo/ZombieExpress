package application.game_use_cases.generate_entity.zombie;

import domain.GamePosition;
import application.game_use_cases.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;

public class GenerateZombieInputData extends GenerateEntityInputData {
    public GenerateZombieInputData(GamePosition position) {
        super(EntityType.ZOMBIE, position);
    }
}
