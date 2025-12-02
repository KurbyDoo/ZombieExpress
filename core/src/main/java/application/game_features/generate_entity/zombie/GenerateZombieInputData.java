package application.game_features.generate_entity.zombie;

import application.game_features.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;
import domain.world.GamePosition;

public class GenerateZombieInputData extends GenerateEntityInputData {
    public GenerateZombieInputData(GamePosition position) {
        super(EntityType.ZOMBIE, position);
    }
}
