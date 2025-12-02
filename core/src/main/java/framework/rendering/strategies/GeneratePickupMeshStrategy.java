package framework.rendering.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import domain.entities.PickupEntity;
import domain.items.Item;
import domain.items.RangedWeapon;
import domain.world.GamePosition;
import framework.physics.GameMesh;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class GeneratePickupMeshStrategy implements GenerateMeshStrategy {

    private final SceneAsset coalAsset;
    private final SceneAsset woodLogAsset;
    private final SceneAsset oilBarrelAsset;
    private final SceneAsset pistolAsset;
    private final SceneAsset rifleAsset;

    private final Texture pistolMetalTexture;
    private final Texture pistolGoldTexture;
    private final Texture pistolRainbowTexture;
    private final Texture rifleBlackTexture;
    private final Texture rifleGoldTexture;
    private final Texture rifleRedTexture;

    public GeneratePickupMeshStrategy() {
        coalAsset = new GLTFLoader().load(Gdx.files.internal("models/items/fuels/coal/coal.gltf"));
        woodLogAsset = new GLTFLoader().load(Gdx.files.internal("models/items/fuels/wood_log/wood_log.gltf"));
        oilBarrelAsset = new GLTFLoader().load(Gdx.files.internal("models/items/fuels/oil_barrel/oil_barrel.gltf"));
        pistolAsset = new GLTFLoader().load(Gdx.files.internal("models/items/weapons/pistols/pistol.gltf"));
        rifleAsset = new GLTFLoader().load(Gdx.files.internal("models/items/weapons/rifles/rifle.gltf"));

        pistolMetalTexture = new Texture(Gdx.files.internal("models/items/weapons/pistols/textures/pistol_metal.jpeg"));
        pistolGoldTexture = new Texture(Gdx.files.internal("models/items/weapons/pistols/textures/pistol_gold.jpeg"));
        pistolRainbowTexture =
            new Texture(Gdx.files.internal("models/items/weapons/pistols/textures/pistol_rainbow.jpeg"));
        rifleBlackTexture = new Texture(Gdx.files.internal("models/items/weapons/rifles/textures/rifle_black.png"));
        rifleGoldTexture = new Texture(Gdx.files.internal("models/items/weapons/rifles/textures/rifle_gold.png"));
        rifleRedTexture = new Texture(Gdx.files.internal("models/items/weapons/rifles/textures/rifle_red.png"));
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

        applySkin(scene, item);

        float yOffset = 0;
        if (item.getName().contains("Pistol")) {
            yOffset = 0.6f;
        } else if (item.getName().contains("Rifle")) {
            yOffset = 1;
        }

        // Position & scale the scene
        float visualScale = getScaleForItemName(item.getName());
        scene.modelInstance.transform.idt();
        scene.modelInstance.transform
            .setToTranslation(pickupPos.x, pickupPos.y + yOffset, pickupPos.z);

        // Build Bullet collision shape from bounding box
        BoundingBox bbox = new BoundingBox();
        scene.modelInstance.calculateBoundingBox(bbox);

        Vector3 halfExtents = new Vector3();
        bbox.getDimensions(halfExtents).scl(0.5f);

        halfExtents.scl(visualScale);

        btCollisionShape shape = new btBoxShape(halfExtents);
        Matrix4 transform = new Matrix4().setToTranslation(pickupPos.x, pickupPos.y + yOffset, pickupPos.z);

        btMotionState motionState = new btDefaultMotionState(transform);
        Vector3 inertia = new Vector3(0, 0, 0); // static
        btRigidBody.btRigidBodyConstructionInfo info =
            new btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, inertia);
        btRigidBody body = new btRigidBody(info);
        info.dispose();

        scene.modelInstance.transform.idt();
        scene.modelInstance.transform
            .setToTranslation(pickupPos.x, pickupPos.y + yOffset, pickupPos.z)
            .scale(visualScale, visualScale, visualScale);

        GameMesh mesh = new GameMesh(inputData.getId(), scene, body, motionState);
        mesh.setStatic(true);
        return mesh;
    }

    private SceneAsset getAssetForItemName(String name) {
        if ("Coal".equals(name)) {
            return coalAsset;
        } else if ("Wood Log".equals(name)) {
            return woodLogAsset;
        } else if ("Oil Barrel".equals(name)) {
            return oilBarrelAsset;
        } else if (name.contains("Rifle")) {
            return rifleAsset;
        } else {
            return pistolAsset;
        }
    }

    private float getScaleForItemName(String name) {
        if ("Coal".equals(name)) {
            return 3;
        } else if ("Wood Log".equals(name)) {
            return 0.4f;
        } else if ("Oil Barrel".equals(name)) {
            return 1.6f;
        } else if (name.contains("Rifle")) {
            return 0.005f;
        } else {
            return 1;
        }
    }

    private void applySkin(Scene scene, Item item) {
        if (!(item instanceof RangedWeapon)) return;

        String name = item.getName();
        Texture overrideTexture = null;

        if ("Combat Pistol".equals(name)) {
            overrideTexture = pistolMetalTexture;
        } else if ("Golden Pistol".equals(name)) {
            overrideTexture = pistolGoldTexture;
        } else if ("Rainbow Pistol".equals(name)) {
            overrideTexture = pistolRainbowTexture;
        } else if ("Tactical Rifle".equals(name)) {
            overrideTexture = rifleBlackTexture;
        } else if ("Golden Rifle".equals(name)) {
            overrideTexture = rifleGoldTexture;
        } else if ("Zombie Obliterator Rifle".equals(name)) {
            overrideTexture = rifleRedTexture;
        }

        for (Material mat : scene.modelInstance.materials) {
            mat.remove(PBRTextureAttribute.BaseColorTexture);
            mat.set(PBRTextureAttribute.createBaseColorTexture(overrideTexture));
        }
    }
}
