package framework.rendering.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import domain.world.GamePosition;
import framework.physics.GameMesh;
import framework.physics.MeshMotionState;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class GenerateTrainMeshStrategy implements GenerateMeshStrategy {

    private static final String TRAIN_MODEL_PATH = "models/train.gltf";

    private SceneAsset trainAsset = new GLTFLoader().load(Gdx.files.internal(TRAIN_MODEL_PATH));
    private Vector3 dimensions;

    @Override
    public GameMesh execute(GenerateMeshInputData inputData) {
        Scene trainScene = new Scene(trainAsset.scene);
        ModelInstance modelInstance = trainScene.modelInstance;
        GamePosition pos = inputData.getEntity().getPosition();
        modelInstance.transform.setToTranslation(pos.x, pos.y, pos.z);

        BoundingBox bbox = new BoundingBox();
        modelInstance.calculateBoundingBox(bbox);
        Vector3 dimensions = new Vector3();
        bbox.getDimensions(dimensions);
        dimensions.scl(0.5f);

        btCollisionShape shape = new btBoxShape(dimensions);


        float mass = 0f;
        Vector3 localInertia = new Vector3(0, 0, 0);

        MeshMotionState motionState =
            new MeshMotionState(modelInstance.transform, new Vector3(0, 0, 0), inputData.getEntity());

        btRigidBody.btRigidBodyConstructionInfo info =
            new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, localInertia);
        btRigidBody body = new btRigidBody(info);

        body.setMotionState(motionState);
        info.dispose();


        GameMesh mesh = new GameMesh(inputData.getId(), trainScene, body, motionState);
        mesh.setShowHitbox(true);

        return mesh;
    }
}
