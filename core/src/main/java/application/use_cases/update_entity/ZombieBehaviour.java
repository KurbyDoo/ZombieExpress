package application.use_cases.update_entity;

import com.badlogic.gdx.math.Vector3;
import domain.entities.Entity;

public class ZombieBehaviour implements EntityBehaviour {
    private final float MOVE_SPEED = 3.0f;
    private final Vector3 tempDir = new Vector3();
    private final Vector3 tempVel = new Vector3();

    @Override
    public void update(Entity entity, BehaviourContext context) {
        // Find player direction
        tempDir.set(context.player.getPosition()).sub(entity.getPosition());

        tempDir.y = 0;
        tempDir.nor();

        // Get physics state
        Vector3 currentVel = context.physics.getLinearVelocity(entity.getID());
        if (currentVel == null) return;

        // Apply movement
        context.physics.setLinearVelocity(
            entity.getID(),
            tempDir.x * MOVE_SPEED,
            currentVel.y,
            tempDir.z * MOVE_SPEED
        );

        context.physics.lookAt(entity.getID(), entity.getPosition().sub(tempDir));
    }
}
