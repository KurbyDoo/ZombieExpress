package application.use_cases.generate_mesh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import domain.entities.Entity;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import physics.GameMesh;

public class GenerateZombieMeshStrategy implements GenerateMeshStrategy {
    // TODO: Dispose asset on game close
    private SceneAsset zombieAsset = new GLTFLoader().load(Gdx.files.internal("models/model.gltf"));

    @Override
    public GameMesh execute(GenerateMeshInputData inputData) {
        Scene zombieScene = new Scene(zombieAsset.scene);
        Entity entity = inputData.getEntity();
        zombieScene.modelInstance.transform.setToTranslation(entity.getPosition());

        BoundingBox bbox = new BoundingBox();
        zombieScene.modelInstance.calculateBoundingBox(bbox);
        Vector3 dimensions = new Vector3();
        bbox.getDimensions(dimensions);

        btCollisionShape shape = new btBoxShape(dimensions.scl(0.5f));

        float mass = 10f; // Example: 10kg
        Vector3 localInertia = new Vector3();
        shape.calculateLocalInertia(mass, localInertia);
        // TODO: This doesnt work for some reason
        btMotionState motionState = new btDefaultMotionState(zombieScene.modelInstance.transform);

        btRigidBody.btRigidBodyConstructionInfo info =
            new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
        btRigidBody body = new btRigidBody(info);
        body.setMotionState(motionState);
        info.dispose();

        body.setAngularFactor(new Vector3(0, 1, 0));

        return new GameMesh(inputData.getId(), zombieScene, body);
    }
}
