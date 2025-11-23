package infrastructure.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import domain.entities.Item;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import domain.entities.WorldPickup;
import domain.entities.ItemTypes;

public class ItemPickupSceneFactory {

    private static final SceneAsset coalAsset = new GLTFLoader().load(Gdx.files.internal("models/items/fuels/coal/coal.gltf"));
    private static final SceneAsset woodLogAsset = new GLTFLoader().load(Gdx.files.internal("models/items/fuels/wood_log/wood_log.gltf"));
    private static final SceneAsset oilBarrelAsset = new GLTFLoader().load(Gdx.files.internal("models/items/fuels/oil_barrel/oil_barrel.gltf"));

    private ItemPickupSceneFactory() {}

    private static float getScaleForItemName(String name) {
        switch (name) {
            case "Coal": return 3.3f;
            case "Wood Log": return 0.3f;
            case "Oil Barrel": return 1.6f;
            default: return 1;
        }
    }

    private static SceneAsset getAssetForItemName(String name) {
        switch (name) {
            case "Coal": return coalAsset;
            case "Oil Barrel": return oilBarrelAsset;
            default: return woodLogAsset;
        }
    }

    /**
     * Create a new Scene instance for this pickup's item.
     * The scene's transform will still need to be positioned by caller.
     */
    public static Scene createSceneForPickup(WorldPickup pickup) {
        Item item = pickup.getItem();
        String name = item.getName();
        SceneAsset asset = getAssetForItemName(name);
        Scene scene = new Scene(asset.scene);

        float scale = getScaleForItemName(name);
        scene.modelInstance.transform.scale(scale, scale, scale);
        scene.modelInstance.transform.setTranslation(pickup.getPosition());
        return scene;
    }
}
