package application.use_cases.update_entity;

import domain.GamePosition;
import domain.entities.Entity;

public class ZombieBehaviour implements EntityBehaviour {
    private final float MOVE_SPEED = 3.0f;
    private final GamePosition tempDir = new GamePosition();
    private final GamePosition tempVel = new GamePosition();

    @Override
    public void update(Entity entity, BehaviourContext context) {
        // Find player direction
        tempDir.set(context.getPlayer().getPosition()).sub(entity.getPosition());
        tempDir.y = 0;
        tempDir.nor();

        // Get physics state
        entity.setVelocity(
            tempDir.x * MOVE_SPEED,
            0,
            tempDir.z * MOVE_SPEED
        );

        // Apply movement
        float yaw = (float) Math.toDegrees(Math.atan2(-tempDir.x, -tempDir.z));
        entity.setYaw(yaw);
    }
}
