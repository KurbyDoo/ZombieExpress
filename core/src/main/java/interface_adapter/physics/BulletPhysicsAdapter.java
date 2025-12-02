package interface_adapter.physics;

import application.ports.PhysicsControlPort;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import domain.world.GamePosition;
import framework.physics.GameMesh;
import framework.rendering.MeshStorage;

public class BulletPhysicsAdapter implements PhysicsControlPort {
    private final MeshStorage meshStorage;
    private final Vector3 tempVec = new Vector3();
    private final Matrix4 tempMat = new Matrix4();
    private final Quaternion tempQuat = new Quaternion();

    public BulletPhysicsAdapter(MeshStorage meshStorage) {
        this.meshStorage = meshStorage;
    }

    private btRigidBody getBody(int entityId) {
        GameMesh mesh = meshStorage.getMesh(entityId);
        if (mesh == null) return null;
        return mesh.getBody();
    }

    @Override
    public void setLinearVelocity(int entityId, float x, float y, float z) {
        btRigidBody body = getBody(entityId);
        if (body != null) {
            // IMPORTANT: You must activate the body, or Bullet ignores the velocity change
            // if the body thinks it is "sleeping" (not moving).
            body.activate();

            tempVec.set(x, y, z);
            body.setLinearVelocity(tempVec);
        }
    }

    @Override
    public GamePosition getLinearVelocity(int entityId) {
        btRigidBody body = getBody(entityId);
        if (body != null) {
            // Return a copy so the caller doesn't modify internal state
            Vector3 vel = body.getLinearVelocity();
            return new GamePosition(vel.x, vel.y, vel.z);
        }
        return new GamePosition(0, 0, 0);
    }

    @Override
    public void lookAt(int entityId, GamePosition targetPosition) {
        btRigidBody body = getBody(entityId);
        GameMesh mesh = meshStorage.getMesh(entityId);

        if (body != null && mesh != null) {
            body.activate();

            // 1. Get current physics position (Center of mass)
            body.getWorldTransform(tempMat);
            Vector3 currentPos = tempMat.getTranslation(tempVec);

            // 2. Calculate direction to target
            Vector3 direction = new Vector3(targetPosition.x, targetPosition.y, targetPosition.z).sub(currentPos);
            direction.y = 0;
            direction.nor();

            // 3. Calculate Rotation (Quaternion)
            // LibGDX ModelInstance usually faces +Z.
            // Calculate the angle from +Z to our direction vector.
            float yaw = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));

            tempQuat.setEulerAngles(yaw, 0, 0);

            // 4. Apply rotation to the Physics Body
            // We get the current transform, reset rotation, apply new rotation
            body.getWorldTransform(tempMat);
            tempMat.set(currentPos, tempQuat);
            body.setWorldTransform(tempMat);

            // 5. Also update the MotionState immediately to prevent visual jitter
            if (body.getMotionState() != null) {
                body.getMotionState().setWorldTransform(tempMat);
            }
        }
    }
}
