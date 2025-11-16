package presentation.view;

import application.use_cases.EntityGeneration.EntityGenerationInputData;
import application.use_cases.EntityGeneration.EntityGenerationInteractor;
import application.use_cases.EntityGeneration.EntityGenerationInputData;
import application.use_cases.EntityGeneration.EntityGenerationInteractor;
import application.use_cases.RenderZombie.RenderZombieInputData;
import application.use_cases.RenderZombie.RenderZombieInteractor;
import domain.entities.Player;
import domain.entities.World;
import domain.entities.ZombieStorage;
import presentation.ZombieInstanceUpdater;
import presentation.controllers.CameraController;
import presentation.controllers.EntityController;
import presentation.controllers.EntityController;
import presentation.controllers.FirstPersonCameraController;
import infrastructure.input_boundary.GameInputAdapter;
import application.use_cases.PlayerMovement.PlayerMovementInputBoundary;
import application.use_cases.PlayerMovement.PlayerMovementInteractor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import io.github.testlibgdx.ChunkLoader;
import infrastructure.rendering.ObjectRenderer;
import presentation.controllers.WorldController;



public class GameView implements Viewable {
    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;

    public ObjectRenderer objectRenderer;
    private CameraController cameraController;
    private GameInputAdapter gameInputAdapter;
    private ViewCamera camera;
    private Player player;
    private WorldController worldController;
    private float accumulator;

    // add EntityController
    private EntityController entityController;
    private EntityGenerationInteractor entityGenerationInteractor;
    private RenderZombieInteractor renderZombieInteractor;

    @Override
    public void createView() {
        Vector3 startingPosition = new Vector3(0, 16f, 0);
        player = new Player(startingPosition);

        camera = new ViewCamera();

        PlayerMovementInputBoundary playerMovementInteractor = new PlayerMovementInteractor(player);

        gameInputAdapter = new GameInputAdapter(playerMovementInteractor);
        Gdx.input.setInputProcessor(gameInputAdapter);
        Gdx.input.setCursorCatched(true);

        cameraController = new FirstPersonCameraController(camera, player);

        objectRenderer = new ObjectRenderer(camera);
        worldController = new WorldController(objectRenderer, 4); // 4 chunk radius;
        //test add entities
//        Zombie zombie = new Zombie(objectRenderer);
//        zombie.createZombie(); //delete this later
        ZombieStorage zombieStorage = new ZombieStorage();
        entityGenerationInteractor = new EntityGenerationInteractor(zombieStorage);
        renderZombieInteractor = new RenderZombieInteractor(zombieStorage);
        ZombieInstanceUpdater zombieInstanceUpdater = new ZombieInstanceUpdater(objectRenderer);

        //entityGenerationInteractor.execute(new EntityGenerationInputData());
        entityController = new EntityController(entityGenerationInteractor, renderZombieInteractor, zombieStorage, zombieInstanceUpdater);
        entityController.generateZombie();
    }

    @Override
    public void renderView() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        accumulator += deltaTime;

        while (accumulator >= TIME_STEP) {
            accumulator -= TIME_STEP;
            cameraController.updatePrevious();

            // --- GAME LOGIC ---
            // 1. Process player input
            gameInputAdapter.processInput(TIME_STEP);
            // 2. Update the world
            worldController.update(player.getPosition());
        }


        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
        objectRenderer.render();
        entityController.renderZombie();
    }

    @Override
    public void disposeView() {
        objectRenderer.dispose();
    }
}
