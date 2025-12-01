package application.game_use_cases.update_entity;

import domain.GamePosition;
import domain.entities.Bullet;
import domain.entities.Entity;

public class BulletBehaviour implements EntityBehaviour {
    private final float BULLET_SPEED = 100;
    private final GamePosition tempDir = new GamePosition();

    @Override
    public void execute(EntityBehaviourInputData inputData) {
        // Bullets usually have their velocity set once at creation.
        // But if we need constant propulsion (like a rocket):

        // Assuming the physics body is already rotated correctly:
        // context.physics.applyCentralForce(entity.getId(), ...);

        // Or simple timeout logic:
        // if (entity.getAge() > 5.0f) destroy(entity);
        // Find player direction

        tempDir.set(((Bullet)inputData.getEntity()).getDirection());

        tempDir.y = 0;
        tempDir.nor();

        // Get physics state
        inputData.getEntity().setVelocity(
            tempDir.x * BULLET_SPEED,
            0,
            tempDir.z * BULLET_SPEED
        );

        // Apply movement
        float yaw = (float) Math.toDegrees(Math.atan2(-tempDir.x, -tempDir.z));
        inputData.getEntity().setYaw(yaw);
    }
}
