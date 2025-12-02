package infrastructure.rendering;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import domain.entities.PlayerEntity;
import domain.repositories.EntityStorage;
import domain.GamePosition;
import domain.entities.Entity;
import domain.entities.Train;
import physics.GameMesh;

/**
 * This class ensures that the Visual Mesh (Infrastructure)
 * always matches the Logical Entity (Domain).
 */
public class EntityMeshSynchronizer {

    private final EntityStorage entityStorage;
    private final MeshStorage meshStorage;

    private final Vector3 tempVec = new Vector3();
    private final Quaternion tempQuat = new Quaternion();
    private final Matrix4 tempMat = new Matrix4();

    public EntityMeshSynchronizer(EntityStorage entityStorage, MeshStorage meshStorage) {
        this.entityStorage = entityStorage;
        this.meshStorage = meshStorage;
    }


    // TODO: Should the sync happen after or before the physics step?
    // Dont we want to "attempt" to move entites first, let physics handle collisions
    // Then update entities to match mesh?
    public void sync() {
        for (Integer id : entityStorage.getAllIds()) {
            if (meshStorage.hasMesh(id)) {
                Entity entity = entityStorage.getEntityByID(id);
                GameMesh mesh = meshStorage.getMesh(id);

                if (entity instanceof Train || entity instanceof PlayerEntity) {
                    syncKinematic(entity, mesh);
                } else {
                    syncDynamic(entity, mesh);
                }
            }
        }
    }

    private void syncKinematic(Entity entity, GameMesh mesh) {
        GamePosition pos = entity.getPosition();

        if (mesh.getBody() != null) {
            btRigidBody body = mesh.getBody();
            // Force Position
            body.getWorldTransform(tempMat);
            tempMat.setTranslation(pos.x, pos.y, pos.z);

            body.setWorldTransform(tempMat);

            if (body.getMotionState() != null) {
                body.getMotionState().setWorldTransform(tempMat);
            }
        }
    }

    private void syncDynamic(Entity entity, GameMesh mesh) {
        if (mesh.getBody() != null) {
            btRigidBody body = mesh.getBody();
            body.activate();

            GamePosition vel = entity.getVelocity();

            Vector3 currentBodyVel = body.getLinearVelocity();
            body.setLinearVelocity(tempVec.set(vel.x, currentBodyVel.y, vel.z));

            body.getWorldTransform(tempMat);
            Vector3 currentPos = tempMat.getTranslation(tempVec);

            tempQuat.setEulerAngles(entity.getYaw(), 0, 0);
            tempMat.set(currentPos, tempQuat);

            body.setWorldTransform(tempMat);
        }
    }
}
