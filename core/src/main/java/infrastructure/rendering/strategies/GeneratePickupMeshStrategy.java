package infrastructure.rendering.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import domain.GamePosition;
import domain.entities.PickupEntity;
import domain.items.Item;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import physics.GameMesh;

public class GeneratePickupMeshStrategy implements GenerateMeshStrategy {

    private final SceneAsset coalAsset;
    private final SceneAsset woodLogAsset;
    private final SceneAsset oilBarrelAsset;

    public GeneratePickupMeshStrategy() {
        coalAsset    = new GLTFLoader().load(Gdx.files.internal("models/items/fuels/coal/coal.gltf"));
        woodLogAsset = new GLTFLoader().load(Gdx.files.internal("models/items/fuels/wood_log/wood_log.gltf"));
        oilBarrelAsset = new GLTFLoader().load(Gdx.files.internal("models/items/fuels/oil_barrel/oil_barrel.gltf"));
    }

    @Override
    public GameMesh execute(GenerateMeshInputData inputData) {
        if (!(inputData.getEntity() instanceof PickupEntity)) {
            throw new IllegalArgumentException("Pickup mesh strategy requires PickupEntity");
        }

        PickupEntity pickup = (PickupEntity) inputData.getEntity();
        GamePosition pickupPos = pickup.getPosition();
        Item item = pickup.getItem();

        SceneAsset asset = getAssetForItemName(item.getName());
        Scene scene = new Scene(asset.scene);

        // Position & scale the scene
        float scale = getScaleForItemName(item.getName());
        scene.modelInstance.transform.idt();
        scene.modelInstance.transform
            .setToTranslation(pickupPos.x, pickupPos.y, pickupPos.z)
            .scale(scale, scale, scale);

        // Build Bullet collision shape from bounding box
        BoundingBox bbox = new BoundingBox();
        scene.modelInstance.calculateBoundingBox(bbox);

        Vector3 halfExtents = new Vector3();
        bbox.getDimensions(halfExtents).scl(0.5f);

        btCollisionShape shape = new btBoxShape(halfExtents);
        Matrix4 transform = new Matrix4().setToTranslation(pickupPos.x, pickupPos.y, pickupPos.z);

        btMotionState motionState = new btDefaultMotionState(transform);
        Vector3 inertia = new Vector3(0, 0, 0); // static
        btRigidBody.btRigidBodyConstructionInfo info =
            new btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, inertia);
        btRigidBody body = new btRigidBody(info);
        info.dispose();

        GameMesh mesh = new GameMesh(inputData.getId(), scene, body, motionState);
        mesh.setStatic(true);
        return mesh;
    }

    private SceneAsset getAssetForItemName(String name) {
        switch (name) {
            case "Wood Log": return woodLogAsset;
            case "Oil Barrel": return oilBarrelAsset;
            default: return coalAsset;
        }
    }

    private float getScaleForItemName(String name) {
        switch (name) {
            case "Coal":     return 3;
            case "Wood Log": return 0.3f;
            case "Oil Barrel": return 1.6f;
            default:         return 1;
        }
    }
}
