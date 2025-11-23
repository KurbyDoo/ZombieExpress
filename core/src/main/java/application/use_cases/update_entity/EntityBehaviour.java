package application.use_cases.update_entity;

import domain.entities.Entity;

public interface EntityBehaviour {
    void update(Entity entity, BehaviourContext context);
}
