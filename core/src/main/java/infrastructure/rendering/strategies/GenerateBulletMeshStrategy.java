/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Infrastructure/Rendering)
 *
 * DESIGN PATTERNS:
 * - Strategy Pattern: Mesh generation strategy.
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package infrastructure.rendering.strategies;

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

public class GenerateBulletMeshStrategy implements GenerateMeshStrategy {
    private SceneAsset bulletAsset = new GLTFLoader().load(Gdx.files.internal("models/bullet/bullet.gltf"));

    @Override
    public GameMesh execute(GenerateMeshInputData inputData) {
        Scene bulletScene = new Scene(bulletAsset.scene);
        ModelInstance modelInstance = bulletScene.modelInstance;

        inputData.getEntity().getPosition().add(0, 1f, 0);

        BoundingBox bbox = new BoundingBox();
        modelInstance.calculateBoundingBox(bbox);
        Vector3 dimensions = new Vector3();
        bbox.getDimensions(dimensions);
        dimensions.scl(1f);

        Vector3 visualOffset = new Vector3(0, -dimensions.y, 0);
        GamePosition pos = inputData.getEntity().getPosition();
        modelInstance.transform.setToTranslation(pos.x, pos.y, pos.z);

        btCollisionShape shape = new btBoxShape(dimensions);
        float mass = 1f;
        Vector3 localInertia = new Vector3();
        shape.calculateLocalInertia(mass, localInertia);

        MeshMotionState motionState = new MeshMotionState(modelInstance.transform, visualOffset, inputData.getEntity());

        btRigidBody.btRigidBodyConstructionInfo info =
            new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, localInertia);
        btRigidBody body = new btRigidBody(info);

        body.setMotionState(motionState);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        body.setAngularFactor(new Vector3(0, 1, 0));

        info.dispose();

        GameMesh mesh = new GameMesh(inputData.getId(), bulletScene, body, motionState);
        mesh.setShowHitbox(true);
        return mesh;
    }
}
