package presentation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import domain.entities.Entity;
import com.badlogic.gdx.math.Vector3;
import domain.entities.ZombieStorage;
import domain.entities.Zombie;
import infrastructure.rendering.ObjectRenderer;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ZombieInstanceUpdater {
    //    private ZombieStorage zombieStorage;
    private ObjectRenderer objectRenderer;
    private ZombieStorage zombieStorage;
    private SceneAsset zombieAsset = new GLTFLoader().load(Gdx.files.internal("models/model.gltf"));
    private Boolean zombieAdded = false; // To prevent infinitely adding zombie to the world
//    private SceneAsset zombieAsset = new GLTFLoader().load(Gdx.files.internal("models/model.gltf"));
//    private SceneAsset zombieAsset = new GLTFLoader().load(Gdx.files.internal("models/factory.gltf"));
//    private SceneAsset zombieAsset = new GLTFLoader().load(Gdx.files.internal("models/graveyard.gltf"));

    public ZombieInstanceUpdater(ObjectRenderer objectRenderer, ZombieStorage zombieStorage) {
        this.objectRenderer = objectRenderer;
        this.zombieStorage = zombieStorage;
    }

    public void updateRenderList() {
        // Create a zombie instance and add to ObjectRender
        List<Zombie> zombies = zombieStorage.getZombies();
        for (Zombie zombie : zombies) {
            if (zombie.isVisible() && !zombieAdded) {
                zombieAdded = true;
                // a scene is a model instance
                Scene zombieInstance = new Scene(zombieAsset.scene);
                zombieInstance.modelInstance.transform.setToTranslation(0, 16f, 0);
//                zombieInstance.modelInstance.transform.setToTranslation(-16f, 0f, 32f);
//                zombieInstance.modelInstance.transform.rotate(Vector3.Y, 180f);
                objectRenderer.addToSceneManager(zombieInstance);
            }
        }
    }
}
