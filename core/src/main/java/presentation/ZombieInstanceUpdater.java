package presentation;

import com.badlogic.gdx.Gdx;
import domain.entities.ZombieStorage;
import domain.entities.Zombie;
import infrastructure.rendering.ObjectRenderer;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.List;

public class ZombieInstanceUpdater {
//    private ZombieStorage zombieStorage;
    private ObjectRenderer objectRenderer;
    private SceneAsset zombieAsset = new GLTFLoader().load(Gdx.files.internal("models/model.gltf"));

    public ZombieInstanceUpdater(ObjectRenderer objectRenderer) {
        this.objectRenderer = objectRenderer;
    }

    public void updateRenderList(ZombieStorage zombieStorage) {
        // Create a zombie instance and add to ObjectRender
        List<Zombie> zombies = zombieStorage.getZombies();
        for(Zombie zombie : zombies){
            if (zombie.isVisible()) {
                // a scene is a model instance
                Scene zombieInstance = new Scene(zombieAsset.scene);
                zombieInstance.modelInstance.transform.setToTranslation(0f, 1f, 0f);
                objectRenderer.addToSceneManager(zombieInstance);
            }
        }
    }
}
