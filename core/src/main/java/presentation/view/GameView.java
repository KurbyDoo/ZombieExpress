package presentation.view;

import application.use_cases.EntityGeneration.EntityGenerationInteractor;
import application.use_cases.PlayerMovement.PlayerMovementInputBoundary;
import application.use_cases.PlayerMovement.PlayerMovementInteractor;
import application.use_cases.RenderZombie.RenderZombieInteractor;
import domain.entities.Player;
import domain.entities.ZombieStorage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import infrastructure.input_boundary.GameInputAdapter;
import infrastructure.rendering.ObjectRenderer;
import physics.CollisionHandler;
import physics.GameMesh;
import physics.HitBox;
import presentation.ZombieInstanceUpdater;
import presentation.controllers.CameraController;
import presentation.controllers.EntityController;
import presentation.controllers.FirstPersonCameraController;
import presentation.controllers.WorldController;

import static physics.HitBox.ShapeTypes.SPHERE;

public class GameView implements Viewable {
    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;

    public ObjectRenderer objectRenderer;
    private CameraController cameraController;
    private GameInputAdapter gameInputAdapter;
    private ViewCamera camera;
    private Player player;

    // --- WORLD & ENTITY MANAGEMENT ---
    private WorldController worldController; // From Left/Middle for chunk management
    private EntityController entityController; // From Right for entity logic
    private EntityGenerationInteractor entityGenerationInteractor;
    private RenderZombieInteractor renderZombieInteractor;
    private CollisionHandler colHandler; // From Right for physics
    private HitBox block; // Physics testing object (From Right)

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

        // --- RENDERER & PHYSICS SETUP (From Right) ---
        colHandler = new CollisionHandler();
        // ObjectRenderer now requires CollisionHandler
        objectRenderer = new ObjectRenderer(camera, colHandler);

        // --- WORLD & CHUNK SETUP (From Left/Middle) ---
        worldController = new WorldController(objectRenderer, 4); // 4 chunk radius;

        // --- PHYSICS TEST OBJECT (From Right) ---
        block = new HitBox("sphere", SPHERE, 10, 10, 60);
        GameMesh red = block.Construct();
        objectRenderer.add(red);

        // --- ENTITY SYSTEM SETUP (From Right) ---
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

            // --- GAME LOGIC ---
            // 1. Process player input
            gameInputAdapter.processInput(TIME_STEP);
            // 2. Update the world
            worldController.update(player.getPosition());
        }


        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
        // Render Entities (From Right)
        entityController.renderZombie();

        // Render Objects (with deltaTime for SceneManager updates) (From Right)
        objectRenderer.render(deltaTime);
    }

    @Override
    public void disposeView() {
        objectRenderer.dispose();
        if (block != null) block.dispose();
    }
}
