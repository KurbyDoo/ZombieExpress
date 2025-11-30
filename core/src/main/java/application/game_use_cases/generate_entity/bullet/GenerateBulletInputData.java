package application.game_use_cases.generate_entity.bullet;

import domain.GamePosition;
import application.game_use_cases.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;

public class GenerateBulletInputData extends GenerateEntityInputData {
    public GenerateBulletInputData(GamePosition position) {
        super(EntityType.BULLET, position);
    }
}
