package presentation.view;

import application.use_cases.EntityGeneration.EntityGenerationInteractor;
import application.use_cases.RenderZombie.RenderZombieInteractor;
import application.use_cases.ports.BlockRepository;
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
import application.use_cases.chunk_generation.ChunkGenerationInteractor;
import application.use_cases.player_movement.PlayerMovementInputBoundary;
import application.use_cases.player_movement.PlayerMovementInteractor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import presentation.controllers.WorldGenerationController;

import static physics.HitBox.ShapeTypes.SPHERE;

/**
 * CLEAN ARCHITECTURE NOTE: 
 * According to the requirement, GameView should act as the main class for dependency injection,
 * not Main.java or ViewManager.java. This is currently being followed correctly.
 * 
 * ARCHITECTURAL ASSESSMENT:
 * GameView currently serves as:
 * 1. Dependency injection container (GOOD - per requirement)
 * 2. Main game loop coordinator (GOOD)
 * 3. Direct instantiation of infrastructure components (ACCEPTABLE but could be improved with factories)
 * 
 * ISSUES IDENTIFIED:
 * 1. Three rendering/collision systems are visible here:
 *    - CollisionHandler + GameMesh (btCollisionObject-based, will migrate to btRigidBody)
 *    - ModelGeneratorFacade + ChunkLoader (chunk mesh rendering via ModelInstance)
 *    - SceneManager (entity rendering via .gltf Scene objects)
 * 
 * 2. Missing abstraction: No unified "Entity" concept that spans both rendering and collision
 * 
 * 3. The "updater" layer concept (ZombieInstanceUpdater) exists but is incomplete:
 *    - Only handles zombie rendering updates
 *    - Does not handle collision mesh synchronization
 *    - Should be generalized to handle all entities
 */
public class GameView implements Viewable{
    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;

    // SYSTEM 1: Rendering infrastructure (mixed ModelBatch + SceneManager)
    public ObjectRenderer objectRenderer;
    public ModelGeneratorFacade meshBuilder;
    
    public World world;
    private CameraController cameraController;
    private GameInputAdapter gameInputAdapter;
    private ViewCamera camera;
    
    // SYSTEM 2: Chunk-based mesh rendering
    private ChunkLoader chunkLoader;
    private WorldGenerationController worldGenerationController;
    
    private Player player;
    private BlockRepository blockRepository;
    private BlockMaterialRepository materialRepository;

    private float accumulator;

    // SYSTEM 3: Collision system (currently uses btCollisionObject, will migrate to btRigidBody)
    private CollisionHandler colHandler;

    // ARCHITECTURAL NOTE: This is test collision object, demonstrates direct GameMesh usage
    private HitBox block;

    // PARTIAL SOLUTION: EntityController + ZombieInstanceUpdater represent the start of the "updater" pattern
    // However, this is specific to zombies and not generalized to all entities
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

        // ARCHITECTURAL ISSUE: CollisionHandler is created here but passed to ObjectRenderer
        // This couples rendering and collision at initialization
        // RECOMMENDATION: Keep these separate - create both independently and coordinate via updater layer
        colHandler = new CollisionHandler();

        // ARCHITECTURAL ISSUE: ObjectRenderer depends on CollisionHandler
        // This violates separation of concerns
        objectRenderer = new ObjectRenderer(camera, colHandler);
        world = new World();
        meshBuilder = new ModelGeneratorFacade(world, blockRepository, materialRepository);
        chunkLoader = new ChunkLoader(meshBuilder, objectRenderer);

        worldGenerationController = new WorldGenerationController(world, chunkLoader, blockRepository);
        worldGenerationController.generateInitialWorld(8, 4, 32);
        
        // ARCHITECTURAL NOTE: Test physics object using GameMesh
        // Demonstrates System #1 (collision) and System #2 (mesh rendering) coupling
        block = new HitBox("sphere", SPHERE, 10, 10, 60);
        GameMesh red = block.Construct();
        objectRenderer.add(red);  // Single call adds to BOTH rendering and collision

        // ARCHITECTURAL NOTE: Entity system demonstration
        // Uses System #3 (scene-based rendering) separately from collision
        ZombieStorage zombieStorage = new ZombieStorage();
        entityGenerationInteractor = new EntityGenerationInteractor(zombieStorage);
        renderZombieInteractor = new RenderZombieInteractor(zombieStorage);
        
        // GOOD PATTERN: ZombieInstanceUpdater is the "updater" layer concept
        // ISSUE: Only works for zombies, needs to be generalized
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
            gameInputAdapter.processInput(deltaTime);

            // Call entity controller and pass world and entity list

        }


        // BACKGROUND PROCESSING
        chunkLoader.loadChunks();

        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
        entityController.renderZombie();
        objectRenderer.render(deltaTime);
    }

    @Override
    public void disposeView() {
        objectRenderer.dispose();
        block.dispose();
    }
}
