package framework.rendering.strategies;

import domain.entities.Entity;

public class GenerateMeshInputData {
    private final Entity entity;
    private final int id;

    public GenerateMeshInputData(Entity entity, int id) {
        this.entity = entity;
        this.id = id;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getId() {
        return id;
    }
}
