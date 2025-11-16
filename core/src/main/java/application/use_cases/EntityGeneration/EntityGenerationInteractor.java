package application.use_cases.EntityGeneration;

import com.badlogic.gdx.math.Vector3;
import domain.entities.ZombieStorage;
import domain.entities.Zombie;

public class EntityGenerationInteractor implements EntityGenerationInputBoundary{
    // Makes changes to the zombies in the storage
    // makes isRendered true

    private ZombieStorage zombieStorage;

    public EntityGenerationInteractor(ZombieStorage zombieStorage) {
        this.zombieStorage = zombieStorage;
    }

    @Override
    public void execute(EntityGenerationInputData inputData) {
        // generate a new zombie at position 0,0,0
        Vector3 position = new Vector3(0f, 0f, 0f);
        Zombie zombie = new Zombie(position);
        //zombie.setRendered(true);
        zombieStorage.addZombie(zombie);

        }
    }
//    private Scene scene;
//    private SceneAsset sceneAsset;
//    private final ObjectRenderer objectRenderer;
//
//    public EntityGenerationInteractor(ObjectRenderer objectRenderer) {
//        System.out.println("EntityGenerationInteractor initialized.");
//        this.objectRenderer = objectRenderer;
//    }
//
//    @Override
//    public void execute(EntityGenerationInputData inputData) {
//        System.out.println("EntityGenerationInteractor executed.");
//        SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/model.gltf"));
//        Scene scene = new Scene(sceneAsset.scene);
//        objectRenderer.addZombieToScene(scene);
//    }

