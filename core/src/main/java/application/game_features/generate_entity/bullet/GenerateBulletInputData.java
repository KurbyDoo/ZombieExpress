package application.game_features.generate_entity.bullet;

import application.game_features.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;
import domain.world.GamePosition;

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
