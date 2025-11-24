package presentation.view;

import application.use_cases.generate_chunk.GenerateChunkInputBoundary;
import application.use_cases.generate_entity.pickup.GeneratePickupStrategy;
import application.use_cases.generate_entity.train.GenerateTrainStrategy;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputBoundary;
import application.use_cases.chunk_mesh_generation.ChunkTexturedMeshGeneration;
import application.use_cases.generate_chunk.GenerateChunkInteractor;
import application.use_cases.generate_entity.zombie.GenerateZombieStrategy;
import application.use_cases.generate_mesh.GeneratePickupMeshStrategy;
import application.use_cases.generate_mesh.GenerateTrainMeshStrategy;
import application.use_cases.generate_mesh.GenerateZombieMeshStrategy;
import application.use_cases.populate_chunk.PopulateChunkEntities;
import application.use_cases.populate_chunk.PopulateChunkInputBoundary;
import application.use_cases.ports.BlockRepository;
import application.use_cases.pickup.PickupInteractor;
import application.use_cases.player_movement.PlayerMovementInputBoundary;
import application.use_cases.player_movement.PlayerMovementInteractor;
import application.use_cases.ports.PhysicsControlPort;
import application.use_cases.update_entity.EntityBehaviourSystem;
import data_access.EntityStorage;
import data_access.InMemoryBlockRepository;
import domain.entities.*;
import domain.items.ItemTypes;
import domain.player.Player;
import domain.World;
import physics.BulletPhysicsAdapter;
import physics.CollisionHandler;
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

    public ObjectRenderer objectRenderer;
    public World world;
    private CameraController cameraController;
    private GameInputAdapter gameInputAdapter;
    private InventoryInputAdapter inventoryInputAdapter;
    private PickUpInputAdapter  pickupInputAdapter;
    private ViewCamera camera;

    private Player player;

    private GenerateChunkInputBoundary chunkGenerator;
    private PopulateChunkInputBoundary chunkPopulator;
    private ChunkMeshGenerationInputBoundary chunkMeshGenerator;
    private WorldSyncController worldSyncController;

    private BlockRepository blockRepository;
    private BlockMaterialRepository materialRepository;

    private float accumulator;

    private CollisionHandler colHandler;

    private EntityBehaviourSystem entityBehaviourSystem;
    private GameSimulationController gameSimulationController;
    private PickupController pickupController;

    private GameHUD hud;

    @Override
    public void createView() {
        world = new World();

        Vector3 startingPosition = new Vector3(0, 3f, 0);
        player = new Player(startingPosition);
        camera = new ViewCamera();

        PlayerMovementInputBoundary playerMovementInteractor = new PlayerMovementInteractor(player);

        gameInputAdapter = new GameInputAdapter(playerMovementInteractor, player);
        Gdx.input.setInputProcessor(gameInputAdapter);
        Gdx.input.setCursorCatched(true);
        inventoryInputAdapter = new InventoryInputAdapter(player);

        cameraController = new FirstPersonCameraController(camera, player);

        blockRepository = new InMemoryBlockRepository();
//        materialRepository = new LibGDXMaterialRepository();
        materialRepository = new TexturedBlockMaterialRepository();

        // --- ENTITY SYSTEM INITIALIZATION ---
        colHandler = new CollisionHandler();

        GenerateZombieStrategy zombieGenerateStrategy = new GenerateZombieStrategy();
        GenerateZombieMeshStrategy zombieMeshStrategy = new GenerateZombieMeshStrategy();

        GenerateTrainStrategy trainGenerateStrategy = new GenerateTrainStrategy();
        GenerateTrainMeshStrategy trainMeshStrategy = new GenerateTrainMeshStrategy();

        GeneratePickupStrategy pickupGenerateStrategy = new GeneratePickupStrategy();
        GeneratePickupMeshStrategy pickupMeshStrategy = new GeneratePickupMeshStrategy();

        EntityStorage entityStorage = new IdToEntityStorage(world);
        EntityFactory entityFactory = new EntityFactory.EntityFactoryBuilder(entityStorage)
            .register(EntityType.ZOMBIE, zombieGenerateStrategy)
            .register(EntityType.TRAIN, trainGenerateStrategy)
            .register(EntityType.PICKUP, pickupGenerateStrategy)
            .build();

        MeshStorage meshStorage = new IdToMeshStorage(colHandler);
        MeshFactory meshFactory = new MeshFactory.MeshFactoryBuilder(meshStorage)
            .register(EntityType.ZOMBIE, zombieMeshStrategy)
            .register(EntityType.TRAIN, trainMeshStrategy)
            .register(EntityType.PICKUP, pickupMeshStrategy)
            .build();

        PickupInteractor pickupInteractor = new PickupInteractor(entityStorage, 3f, 25f);
        pickupController = new PickupController(player, pickupInteractor, meshStorage);
        pickupInputAdapter = new PickUpInputAdapter(pickupController);

        // --- MESH + COL ---
        objectRenderer = new ObjectRenderer(camera, colHandler, meshStorage);

        // --- PHYSICS ---
        PhysicsControlPort physicsAdapter = new BulletPhysicsAdapter(meshStorage);
        entityBehaviourSystem = new EntityBehaviourSystem(physicsAdapter, player, entityStorage, world);

        // --- CHUNK SYSTEM INITIALIZATION ---
        this.chunkGenerator = new GenerateChunkInteractor(blockRepository);
        this.chunkPopulator = new PopulateChunkEntities(entityFactory);
        this.chunkMeshGenerator = new ChunkTexturedMeshGeneration(blockRepository, (TexturedBlockMaterialRepository) materialRepository);

        worldSyncController = new WorldSyncController(
            objectRenderer,
            world,
            player,
            entityFactory,
            entityStorage,
            meshFactory,
            meshStorage,
            chunkGenerator,
            chunkPopulator,
            chunkMeshGenerator,
            RENDER_RADIUS
        );

        gameSimulationController = new GameSimulationController(worldSyncController, colHandler, entityBehaviourSystem, world);
        hud = new GameHUD(player, pickupController);

//        // === TEMP TEST PICKUPS ===
//        int nextPickupId = 1000; // just make sure it doesn’t collide with other entities
//
//        PickupEntity coalPickup = new PickupEntity(
//            nextPickupId++,
//            ItemTypes.COAL,
//            new Vector3(5, 2, 0),
//            true
//        );
//        entityStorage.setIDEntityPair(coalPickup.getID(), coalPickup);
//        GenerateMeshInputData coalMeshData = new GenerateMeshInputData(coalPickup, coalPickup.getID());
//        meshFactory.createMesh(coalMeshData);
//
//        PickupEntity woodPickup = new PickupEntity(
//            nextPickupId++,
//            ItemTypes.WOOD_LOG,
//            new Vector3(7, 2, 0),
//            true
//        );
//        entityStorage.setIDEntityPair(woodPickup.getID(), woodPickup);
//        GenerateMeshInputData woodMeshData = new GenerateMeshInputData(woodPickup, woodPickup.getID());
//        meshFactory.createMesh(woodMeshData);

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

        player.updatePassiveHealing(deltaTime);

        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
        pickupController.refreshPickupTarget();
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
