package application.game_use_cases.update_entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import domain.GamePosition;
import domain.entities.Entity;

public class BulletBehaviour implements EntityBehaviour {

    private final float BULLET_SPEED = 1.0f;
    private final GamePosition tempDir = new GamePosition();
    private final GamePosition tempVel = new GamePosition();

    @Override
    public void update(Entity entity, BehaviourContext context) {
        // Bullets usually have their velocity set once at creation.
        // But if we need constant propulsion (like a rocket):

        // Assuming the physics body is already rotated correctly:
        // context.physics.applyCentralForce(entity.getId(), ...);

        // Or simple timeout logic:
        // if (entity.getAge() > 5.0f) destroy(entity);

//        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
//            tempDir.set(context.getPlayer().getDirection());
//            tempDir.y = 0;
//            tempDir.nor();
//
//            // Get physics state
//            entity.setVelocity(
//                tempDir.x * BULLET_SPEED,
//                tempDir.y * BULLET_SPEED,
//                tempDir.z * BULLET_SPEED
//            );
//
//            // Apply movement
//            // rotate the bullet to face movement direction
//            float yaw = (float) Math.toDegrees(Math.atan2(-tempDir.x, -tempDir.z));
//            entity.setYaw(yaw);
//        }
        tempDir.set(context.getPlayer().getDirection());
        tempDir.y = 0;
        tempDir.nor();

        // Get physics state
        entity.setVelocity(
            tempDir.x * BULLET_SPEED,
            tempDir.y * BULLET_SPEED,
            tempDir.z * BULLET_SPEED
        );

        // Apply movement
        // rotate the bullet to face movement direction
        float yaw = (float) Math.toDegrees(Math.atan2(-tempDir.x, -tempDir.z));
        entity.setYaw(yaw);
    }
}
