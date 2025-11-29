package application.game_use_cases.update_entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import domain.entities.Entity;

public class BulletBehaviour implements EntityBehaviour {

    private final float BULLET_SPEED = 10.0f;
    private final Vector3 tempDir = new Vector3();
    private final Vector3 tempVel = new Vector3();

    @Override
    public void update(Entity entity, BehaviourContext context) {
        // Bullets usually have their velocity set once at creation.
        // But if we need constant propulsion (like a rocket):

        // Assuming the physics body is already rotated correctly:
        // context.physics.applyCentralForce(entity.getId(), ...);

        // Or simple timeout logic:
        // if (entity.getAge() > 5.0f) destroy(entity);

        // TODO: Move input processing to context
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            tempDir.set(context.player.getDirection()).nor();

            Vector3 currentVel = context.physics.getLinearVelocity(entity.getID());
            if (currentVel == null) return;

            // Apply velocity to bullet body
            context.physics.setLinearVelocity(
                entity.getID(),
                tempDir.x * BULLET_SPEED,
                currentVel.y,
                tempDir.z * BULLET_SPEED
            );

            // lookAt is not working for bullet?
            //context.physics.lookAt(entity.getID(), context.player.getDirection());

        }
    }
}
