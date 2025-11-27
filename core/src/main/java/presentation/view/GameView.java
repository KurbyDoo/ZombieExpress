package presentation.view;

import application.use_cases.exit_game.ExitGameUseCase;
import application.use_cases.generate_chunk.GenerateChunkInputBoundary;
import application.use_cases.generate_entity.pickup.GeneratePickupStrategy;
import application.use_cases.generate_entity.train.GenerateTrainStrategy;
import application.use_cases.generate_chunk.GenerateChunkInteractor;
import application.use_cases.generate_entity.zombie.GenerateZombieStrategy;
import application.use_cases.populate_chunk.PopulateChunkEntities;
import application.use_cases.populate_chunk.PopulateChunkInputBoundary;
import application.use_cases.ports.ApplicationLifecyclePort;
import data_access.EntityStorage;
import domain.GamePosition;
import infrastructure.rendering.strategies.GeneratePickupMeshStrategy;
import infrastructure.rendering.strategies.GenerateTrainMeshStrategy;
import infrastructure.rendering.strategies.GenerateZombieMeshStrategy;
import application.use_cases.ports.BlockRepository;
import application.use_cases.pickup.PickupInteractor;
import application.use_cases.player_movement.PlayerMovementInputBoundary;
import application.use_cases.player_movement.PlayerMovementInteractor;
import application.use_cases.ports.PhysicsControlPort;
import application.use_cases.render_radius.RenderRadiusManagerInputBoundary;
import application.use_cases.render_radius.RenderRadiusManagerInteractor;
import application.use_cases.update_entity.EntityBehaviourSystem;
import data_access.InMemoryBlockRepository;
import domain.entities.*;
import domain.player.Player;
import domain.World;
import physics.BulletPhysicsAdapter;
import physics.CollisionHandler;
import physics.GameMesh;
import presentation.controllers.*;
import presentation.view.hud.GameHUD;
import infrastructure.rendering.*;
import infrastructure.input_boundary.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class GameView implements Viewable{
    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;
    private final int RENDER_RADIUS = 6; // The radius in chunks where meshes are visible

//    private ViewCamera camera;
    public ObjectRenderer objectRenderer;
//    public World world;
    private CameraController cameraController;
    // TODO: These should be merged
    private GameInputAdapter gameInputAdapter;
    private InventoryInputAdapter inventoryInputAdapter;
    private PickUpInputAdapter  pickupInputAdapter;

//    private Player player;

    // World Generation
//    private GenerateChunkInteractor chunkGenerator;
//    private ChunkMeshGenerator chunkMeshGenerator;
//    private RenderRadiusManagerInputBoundary renderRadiusManager;

//    private GenerateChunkInputBoundary chunkGenerator;
//    private PopulateChunkInputBoundary chunkPopulator;
//    private ChunkMeshGenerationInputBoundary chunkMeshGenerator;
    private WorldSyncController worldSyncController;

//    private BlockRepository blockRepository;
//    private BlockMaterialRepository materialRepository;
//    private PickupStorage pickupStorage;

    private float accumulator;

//    private CollisionHandler colHandler;

//    private EntityBehaviourSystem entityBehaviourSystem;
    private GameSimulationController gameSimulationController;
    private PickupController pickupController;

    private EntityStorage entityStorage;
    private GameHUD hud;

    // TODO: Merge with entity storage
//    private final Map<WorldPickup, GameMesh> pickupMeshes = new HashMap<>();

    @Override
    public void createView() {
        // ---  WORLD GENERATION SYSTEM INITIALIZATION ---
        World world = new World();
        BlockRepository blockRepository = new InMemoryBlockRepository();

        GamePosition startingPosition = new GamePosition(0, 3f, 0);
        Player player = new Player(startingPosition);

        PlayerMovementInputBoundary playerMovementInteractor = new PlayerMovementInteractor(player);

        // Entity Generation
        GenerateZombieStrategy zombieGenerateStrategy = new GenerateZombieStrategy();
        GenerateTrainStrategy trainGenerateStrategy = new GenerateTrainStrategy();
        GeneratePickupStrategy pickupGenerateStrategy = new GeneratePickupStrategy();

        entityStorage = new IdToEntityStorage(world);
        EntityFactory entityFactory = new EntityFactory.EntityFactoryBuilder(entityStorage)
            .register(EntityType.ZOMBIE, zombieGenerateStrategy)
            .register(EntityType.TRAIN, trainGenerateStrategy)
            .register(EntityType.PICKUP, pickupGenerateStrategy)
            .build();


        ApplicationLifecyclePort lifecycleAdapter = new LibGDXLifecycleAdapter();
        ExitGameUseCase exitGameUseCase = new ExitGameUseCase(lifecycleAdapter);

        gameInputAdapter = new GameInputAdapter(playerMovementInteractor, exitGameUseCase, player);
        Gdx.input.setInputProcessor(gameInputAdapter);
        Gdx.input.setCursorCatched(true);
        inventoryInputAdapter = new InventoryInputAdapter(player);


        // Chunk Generation
        GenerateChunkInteractor chunkGenerator = new GenerateChunkInteractor(blockRepository);
        PopulateChunkInputBoundary chunkPopulator = new PopulateChunkEntities(entityFactory);
        RenderRadiusManagerInteractor renderRadiusManager = new RenderRadiusManagerInteractor(world);

        // --- WORLD RENDERING SYSTEM INITIALIZATION ---
        BlockMaterialRepository materialRepository = new TexturedBlockMaterialRepository();
        ChunkMeshGenerator chunkMeshGenerator = new ChunkMeshGenerator(world, blockRepository, (TexturedBlockMaterialRepository) materialRepository);
        GenerateZombieMeshStrategy zombieMeshStrategy = new GenerateZombieMeshStrategy();
        GenerateTrainMeshStrategy trainMeshStrategy = new GenerateTrainMeshStrategy();
        GeneratePickupMeshStrategy pickupMeshStrategy = new GeneratePickupMeshStrategy();

        CollisionHandler colHandler = new CollisionHandler();

        MeshStorage meshStorage = new IdToMeshStorage(colHandler);
        MeshFactory meshFactory = new MeshFactory.MeshFactoryBuilder(meshStorage)
            .register(EntityType.ZOMBIE, zombieMeshStrategy)
            .register(EntityType.TRAIN, trainMeshStrategy)
            .register(EntityType.PICKUP, pickupMeshStrategy)
            .build();

        // TODO: invert this dependency, object renderer should be at the end
        // --- SETUP FRAMEWORKS ---
        ViewCamera camera = new ViewCamera();
        PickupInteractor pickupInteractor = new PickupInteractor(entityStorage, player);
        pickupController = new PickupController(pickupInteractor, meshStorage);
        pickupInputAdapter = new PickUpInputAdapter(pickupController);

        // --- MESH + COL ---
        objectRenderer = new ObjectRenderer(camera, colHandler, meshStorage);
        cameraController = new FirstPersonCameraController(camera, player);

        // CHUNK + ENTITY RENDERING
        EntityRenderer entityRenderer = new EntityRenderer(entityStorage, meshFactory, meshStorage);
        ChunkRenderer chunkRenderer = new ChunkRenderer(objectRenderer, chunkMeshGenerator, entityRenderer);

        // --- PHYSICS ---
        PhysicsControlPort physicsAdapter = new BulletPhysicsAdapter(meshStorage);
        EntityBehaviourSystem entityBehaviourSystem = new EntityBehaviourSystem(physicsAdapter, player, entityStorage, world);

        worldSyncController = new WorldSyncController(
            RENDER_RADIUS,
            world,
            player,
            renderRadiusManager,
            chunkGenerator,
            chunkPopulator,
            chunkRenderer
        );

        gameSimulationController = new GameSimulationController(worldSyncController, colHandler, entityBehaviourSystem, world);
        hud = new GameHUD(player, entityStorage, pickupController);
    }

    @Override
    public void renderView() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        accumulator += deltaTime;

        while (accumulator >= TIME_STEP) {
            accumulator -= TIME_STEP;
            cameraController.updatePrevious();

            // --- GAME LOGIC ---
            gameInputAdapter.processInput(TIME_STEP);

            inventoryInputAdapter.pollInput();
            pickupInputAdapter.pollInput();
            gameSimulationController.update(TIME_STEP);
        }

        // TODO: This needs to be moved into world
//        player.updatePassiveHealing(deltaTime);

        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
        pickupController.refreshTarget();
        objectRenderer.render(deltaTime);

        // HUD
        hud.update(deltaTime);
        hud.render();
    }

    @Override
    public void disposeView() {
        // Dispose world-related components first
        worldSyncController.dispose();

        objectRenderer.dispose();
    }
}
