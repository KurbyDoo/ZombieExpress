package application.use_cases.RenderZombie;

import application.use_cases.EntityGeneration.EntityGenerationInputBoundary;
import application.use_cases.EntityGeneration.EntityGenerationInputData;
import com.badlogic.gdx.Gdx;
import domain.entities.ZombieStorage;
import domain.entities.Zombie;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.List;

public class RenderZombieInteractor implements RenderZombieInputBoundary {
    // Set zombie.isRendered() to true

    private ZombieStorage zombieStorage;
    private SceneAsset zombieAsset = new GLTFLoader().load(Gdx.files.internal("models/model.gltf"));

    public RenderZombieInteractor(ZombieStorage zombieStorage) {
        this.zombieStorage = zombieStorage;
    }

    @Override
    public void execute(RenderZombieInputData inputData) {
        List<Zombie> zombies = zombieStorage.getZombies();
        for (Zombie zombie : zombies) {
            zombie.setVisible(true);
        }
    }
}
