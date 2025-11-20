package presentation.view;

import application.use_cases.EntityGeneration.EntityGenerationInteractor;
import application.use_cases.RenderZombie.RenderZombieInteractor;
import application.use_cases.ports.BlockRepository;
import application.use_cases.player_movement.PlayerMovementInputBoundary;
import application.use_cases.player_movement.PlayerMovementInteractor;
import data_access.InMemoryBlockRepository;
import domain.entities.Player;
import domain.entities.World;
import physics.CollisionHandler;
import physics.GameMesh;
import physics.HitBox;
import domain.entities.ZombieStorage;
import presentation.ZombieInstanceUpdater;
import infrastructure.rendering.*;
import presentation.controllers.CameraController;
import presentation.controllers.EntityController;
import presentation.controllers.FirstPersonCameraController;
import infrastructure.input_boundary.GameInputAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import presentation.controllers.WorldController;

import static physics.HitBox.ShapeTypes.SPHERE;

public class GameView implements Viewable{
    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;
    private final int RENDER_RADIUS = 6;

    public ObjectRenderer objectRenderer;
    public World world;
    private CameraController cameraController;
    private GameInputAdapter gameInputAdapter;
    private ViewCamera camera;

    private Player player;

    private WorldController worldController;

    private BlockRepository blockRepository;
    private BlockMaterialRepository materialRepository;

    private float accumulator;

    private CollisionHandler colHandler;

    private HitBox block;

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

        blockRepository = new InMemoryBlockRepository();
        materialRepository = new LibGDXMaterialRepository();

        colHandler = new CollisionHandler();
        objectRenderer = new ObjectRenderer(camera, colHandler);
        world = new World();

        // --- CHUNK SYSTEM INITIALIZATION (DELEGATED TO WORLD CONTROLLER) ---
        this.worldController = new WorldController(
            this.objectRenderer,
            this.world,
            this.player,
            this.blockRepository,
            this.materialRepository
        );

        // physics testing
        block = new HitBox("sphere", SPHERE, 10, 10, 60);
        GameMesh red = block.Construct();
        objectRenderer.add(red);

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

            // 2. Update the world logic (Chunk Generation/Meshing/Removal)
            worldController.update();
        }

        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
        entityController.renderZombie();
        objectRenderer.render(deltaTime);
    }


    @Override
    public void disposeView() {
        // Dispose world-related components first
        worldController.dispose();

        objectRenderer.dispose();
        block.dispose();
    }
}
