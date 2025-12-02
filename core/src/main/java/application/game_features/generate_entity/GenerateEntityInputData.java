package application.game_features.generate_entity;

import domain.entities.EntityType;
import domain.world.GamePosition;

public class GenerateEntityInputData {
    private final EntityType type;
    private final GamePosition position;
    private int id;

    public GenerateEntityInputData(EntityType type, GamePosition position) {
        this.type = type;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EntityType getType() {
        return type;
    }

    public GamePosition getPosition() {
        return position;
    }
}
