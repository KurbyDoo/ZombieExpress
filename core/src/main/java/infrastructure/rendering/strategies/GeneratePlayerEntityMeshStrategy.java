package infrastructure.rendering.strategies;

import application.game_use_cases.generate_entity.GenerateEntityInputData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import domain.GamePosition;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import physics.GameMesh;
import physics.MeshMotionState;

public class GeneratePlayerEntityMeshStrategy implements GenerateMeshStrategy{
private final SceneAsset playerAsset;

    public GeneratePlayerEntityMeshStrategy() {
        this.playerAsset = new GLTFLoader().load(Gdx.files.internal("models/bullet/bullet.gltf"));
    }

    @Override
    public GameMesh execute(GenerateMeshInputData inputData) {
        // 1. Create the Visual Scene
        Scene playerScene = new Scene(playerAsset.scene);
        ModelInstance modelInstance = playerScene.modelInstance;
        GamePosition pos = inputData.getEntity().getPosition();
        modelInstance.transform.setToTranslation(pos.x, pos.y, pos.z);

        // Make the model invisible
        for (Material material : modelInstance.materials) {
            material.set(new BlendingAttribute(true, 0f));
        }

        btCollisionShape shape = new btBoxShape(new Vector3(1f, 2f, 1f));
        MeshMotionState motionState = new MeshMotionState(modelInstance.transform, new Vector3(0, 0, 0), inputData.getEntity());

        float mass = 80f;
        Vector3 localInertia = new Vector3();
        shape.calculateLocalInertia(mass, localInertia);

        btRigidBody.btRigidBodyConstructionInfo info =
            new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, localInertia);

        btRigidBody body = new btRigidBody(info);
        body.setMotionState(motionState);
        body.setAngularFactor(new Vector3(0, 0, 0)); // Don't fall over
        info.dispose();

        GameMesh mesh = new GameMesh(inputData.getId(), playerScene, body, motionState);
        mesh.setShowHitbox(true);

        return mesh;
    }
}
