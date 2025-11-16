package presentation.view;

import application.use_cases.EntityGeneration.EntityGenerationInteractor;
import application.use_cases.RenderZombie.RenderZombieInteractor;
import domain.entities.Player;
import domain.entities.ZombieStorage;
import physics.CollisionHandler;
import physics.GameMesh;
import physics.HitBox;
import presentation.ZombieInstanceUpdater;
import infrastructure.rendering.ObjectRenderer;
import presentation.controllers.CameraController;
import presentation.controllers.EntityController;
import presentation.controllers.FirstPersonCameraController;
import infrastructure.input_boundary.GameInputAdapter;
import application.use_cases.PlayerMovement.PlayerMovementInputBoundary;
import application.use_cases.PlayerMovement.PlayerMovementInteractor;
import presentation.controllers.WorldController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

import static physics.HitBox.ShapeTypes.SPHERE;


public class GameView implements Viewable {
    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;

    public ObjectRenderer objectRenderer;
    private CameraController cameraController;
    private GameInputAdapter gameInputAdapter;
    private ViewCamera camera;
    private Player player;

    // Left: Refactored World Controller
    private WorldController worldController;

    // Right: Entity & Physics Controllers
    private EntityController entityController;
    private EntityGenerationInteractor entityGenerationInteractor;
    private RenderZombieInteractor renderZombieInteractor;
    private CollisionHandler colHandler;
    private HitBox block; // Physics testing object

    private float accumulator;

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

        // Right: Initialize Collision Handler
        colHandler = new CollisionHandler();
        // Right: ObjectRenderer now requires CollisionHandler
        objectRenderer = new ObjectRenderer(camera, colHandler);

        // Left: Initialize WorldController (Replaces static WorldGenerationController)
        worldController = new WorldController(objectRenderer, 4);

        // Right: Physics Test Object
        block = new HitBox("sphere", SPHERE, 10, 10, 60);
        GameMesh red = block.Construct();
        objectRenderer.add(red);

        // Right: Entity System Setup
        ZombieStorage zombieStorage = new ZombieStorage();
        entityGenerationInteractor = new EntityGenerationInteractor(zombieStorage);
        renderZombieInteractor = new RenderZombieInteractor(zombieStorage);
        ZombieInstanceUpdater zombieInstanceUpdater = new ZombieInstanceUpdater(objectRenderer);

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

            // WORLD UPDATES
            gameInputAdapter.processInput(TIME_STEP);
            // Update chunks based on player position:
            chunkRadiusManager.execute(player.getPosition());

        }

        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
        // Right: Render Entities
        entityController.renderZombie();

        // Right: Render Objects (with delta)
        objectRenderer.render(deltaTime);
    }

    @Override
    public void disposeView() {
        objectRenderer.dispose();
        if (block != null) block.dispose();
    }
}
