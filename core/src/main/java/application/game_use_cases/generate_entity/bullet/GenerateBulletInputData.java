package application.game_use_cases.generate_entity.bullet;

import domain.GamePosition;
import application.game_use_cases.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;

public class GenerateBulletInputData extends GenerateEntityInputData {

    private final GamePosition direction;
    public GenerateBulletInputData(GamePosition position, GamePosition direction) {
        super(EntityType.BULLET, position);
        this.direction = direction;
    }

    public GamePosition getDirection() {
        return direction;
    }
}
