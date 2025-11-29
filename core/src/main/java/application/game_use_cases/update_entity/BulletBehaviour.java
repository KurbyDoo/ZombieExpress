package application.game_use_cases.update_entity;

import domain.entities.Entity;

public class BulletBehaviour implements EntityBehaviour {
    private final float BULLET_SPEED = 20.0f;

    @Override
    public void update(Entity entity, BehaviourContext context) {
        // Bullets usually have their velocity set once at creation.
        // But if we need constant propulsion (like a rocket):

        // Assuming the physics body is already rotated correctly:
        // context.physics.applyCentralForce(entity.getId(), ...);

        // Or simple timeout logic:
        // if (entity.getAge() > 5.0f) destroy(entity);
    }
}
