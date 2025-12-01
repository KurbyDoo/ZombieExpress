package application.game_use_cases.update_entity;

import domain.entities.Entity;

public class EntityBehaviourInputData {
    private final Entity entity;
    private final float deltaTime;

    public EntityBehaviourInputData(Entity entity, float deltaTime) {
        this.entity = entity;
        this.deltaTime = deltaTime;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getDeltaTime() {
        return deltaTime;
    }
}
