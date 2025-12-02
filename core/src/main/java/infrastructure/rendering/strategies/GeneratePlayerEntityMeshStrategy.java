package infrastructure.rendering.strategies;

import application.game_use_cases.generate_entity.GenerateEntityInputData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
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
//    private final SceneAsset playerAsset = null;
// We hold the asset here.
// In a final game, you'd want to dispose of this when the game closes.
private final SceneAsset playerAsset;

    public GeneratePlayerEntityMeshStrategy() {
        // LOAD ASSET HERE directly as you requested
        // Ensure "models/bullet/bullet.gltf" exists in your assets folder!
        this.playerAsset = new GLTFLoader().load(Gdx.files.internal("models/bullet/bullet.gltf"));
    }

    @Override
    public GameMesh execute(GenerateMeshInputData inputData) {
        // 1. Create the Visual Scene
        Scene playerScene = new Scene(playerAsset.scene);
        ModelInstance modelInstance = playerScene.modelInstance;

        // 2. Set Initial Position
        Vector3 pos = new Vector3(inputData.getEntity().getPosition().x, inputData.getEntity().getPosition().y, inputData.getEntity().getPosition().z);
        modelInstance.transform.setToTranslation(pos);

        // 3. Define the Hitbox Size MANUALLY (Since the bullet model is tiny, we fake a Player size)
        // A standard player is ~1.8m tall, 0.6m wide.
        // Half-extents: 0.3, 0.9, 0.3
        Vector3 halfExtents = new Vector3(3f, 9f, 3f);
        btCollisionShape shape = new btBoxShape(halfExtents);

        // 4. Setup Motion State
        // Offset the visual model if needed (e.g. if the model pivot is at the center, move it down)
        Vector3 visualOffset = new Vector3(0, -0.9f, 0);
        MeshMotionState motionState = new MeshMotionState(modelInstance.transform, visualOffset, inputData.getEntity());

        // 5. Create the Body
        float mass = 80f;
        Vector3 localInertia = new Vector3();
        shape.calculateLocalInertia(mass, localInertia);

        btRigidBody.btRigidBodyConstructionInfo info =
            new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, localInertia);

        btRigidBody body = new btRigidBody(info);

        // Physics Settings
        body.setActivationState(Collision.DISABLE_DEACTIVATION); // Never sleep
        body.setAngularFactor(new Vector3(0, 0, 0)); // Don't fall over
        body.setFriction(0f); // Don't stick to walls

        info.dispose();

        // 6. Return the GameMesh
        GameMesh mesh = new GameMesh(inputData.getId(), playerScene, body, motionState);
        mesh.setShowHitbox(true); // Draw the green box so you can see the size!

        return mesh;
    }
}
