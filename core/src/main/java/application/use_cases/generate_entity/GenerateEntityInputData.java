package application.use_cases.generate_entity;

import com.badlogic.gdx.math.Vector3;
import domain.Chunk;
import domain.entities.EntityType;

public class GenerateEntityInputData {
    private int id;
    private final EntityType type;
    private final Vector3 position;
    private final Chunk chunk;

    public GenerateEntityInputData(EntityType type, Vector3 position, Chunk chunk) {
        this.type = type;
        this.position = position;
        this.chunk = chunk;
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

    public Vector3 getPosition() {
        return position;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
