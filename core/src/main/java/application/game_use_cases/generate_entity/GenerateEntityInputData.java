package application.game_use_cases.generate_entity;

import domain.GamePosition;
import domain.Chunk;
import domain.entities.EntityType;

public class GenerateEntityInputData {
    private int id;
    private final EntityType type;
    private final GamePosition position;

    public GenerateEntityInputData(EntityType type, GamePosition position) {
        this.type = type;
        this.position = position;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public EntityType getType() {
        return type;
    }

    public GamePosition getPosition() {
        return position;
    }
}
