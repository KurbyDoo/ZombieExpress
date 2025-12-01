package application.game_features.generate_entity.zombie;

import domain.GamePosition;
import application.game_features.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;

public class GenerateZombieInputData extends GenerateEntityInputData {
    public GenerateZombieInputData(GamePosition position) {
        super(EntityType.ZOMBIE, position);
    }
}
