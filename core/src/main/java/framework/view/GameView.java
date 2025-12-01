package framework.view;

import application.game_features.update_entity.BulletBehaviour;
import application.game_features.update_entity.TrainBehaviour;
import application.game_features.update_entity.ZombieBehaviour;
import application.game_features.win_condition.WinConditionInputBoundary;
import application.game_features.win_condition.WinConditionInteractor;
import application.game_features.dismount_entity.DismountEntityInputBoundary;
import application.game_features.dismount_entity.DismountEntityInteractor;
import application.game_features.exit_game.ExitGameUseCase;
import application.game_features.generate_chunk.GenerateChunkInputBoundary;
import application.game_features.generate_entity.pickup.GeneratePickupStrategy;
import application.game_features.generate_entity.train.GenerateTrainStrategy;
import application.game_features.generate_chunk.GenerateChunkInteractor;
import application.game_features.generate_entity.zombie.GenerateZombieStrategy;
import application.game_features.mount_entity.MountEntityInputBoundary;
import application.game_features.mount_entity.MountEntityInteractor;
import application.game_features.update_world.UpdateWorldInputBoundary;
import application.game_features.update_world.UpdateWorldInteractor;
import application.game_features.populate_chunk.PopulateChunkInteractor;
import application.game_features.populate_chunk.PopulateChunkInputBoundary;
import application.game_features.ports.ApplicationLifecyclePort;
import application.game_features.win_condition.WinConditionOutputData;
import framework.data_access.IdToEntityStorage;
import application.gateways.EntityStorage;
import domain.GamePosition;
import framework.rendering.*;
import framework.rendering.strategies.GeneratePickupMeshStrategy;
import framework.rendering.strategies.GenerateTrainMeshStrategy;
import framework.rendering.strategies.GenerateZombieMeshStrategy;
import application.gateways.BlockRepository;
import application.game_features.pickup.PickupInteractor;
import application.game_features.player_movement.PlayerMovementInputBoundary;
import application.game_features.player_movement.PlayerMovementInteractor;
import application.game_features.ports.PhysicsControlPort;
import application.game_features.render_radius.RenderRadiusManagerInputBoundary;
import application.game_features.render_radius.RenderRadiusManagerInteractor;
import application.game_features.update_entity.EntityBehaviourSystem;
import framework.data_access.InMemoryBlockRepository;
import domain.entities.*;
import domain.player.Player;
import domain.World;
import interface_adapter.controllers.*;
import interface_adapter.input.GameInputAdapter;
import interface_adapter.input.InventoryInputAdapter;
import interface_adapter.input.LibGDXLifecycleAdapter;
import interface_adapter.input.PickUpInputAdapter;
import interface_adapter.physics.BulletPhysicsAdapter;
import framework.physics.CollisionHandler;
import presentation.controllers.*;
import framework.view.hud.GameHUD;
import infrastructure.rendering.*;
import infrastructure.input_boundary.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

public class GameView implements Viewable{
    private boolean initialized = false;

    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;
    private final int RENDER_RADIUS = 6; // The radius in chunks where meshes are visible

    public ObjectRenderer objectRenderer;
    private CameraController cameraController;
    // TODO: These should be merged
    private GameInputAdapter gameInputAdapter;
    private InventoryInputAdapter inventoryInputAdapter;
    private PickUpInputAdapter pickupInputAdapter;

    private WorldSyncController worldSyncController;
    private float accumulator;

    private GameSimulationController gameSimulationController;
    private PickupController pickupController;
    private WinConditionInputBoundary WinConditionInteractor;

    private GameHUD hud;

    @Override
    public void createView() {
        // ---  WORLD GENERATION SYSTEM INITIALIZATION ---
        World world = new World();
        BlockRepository blockRepository = new InMemoryBlockRepository();

        GamePosition startingPosition = new GamePosition(0, 3f, 0);
        Player player = new Player(startingPosition);
        MountEntityInputBoundary mountEntity = new MountEntityInteractor(player);
        DismountEntityInputBoundary dismountEntity = new DismountEntityInteractor(player);

        PlayerMovementInputBoundary playerMovementInteractor = new PlayerMovementInteractor(player);

        // Entity Generation
        GenerateZombieStrategy zombieGenerateStrategy = new GenerateZombieStrategy();
        GenerateTrainStrategy trainGenerateStrategy = new GenerateTrainStrategy();
        GeneratePickupStrategy pickupGenerateStrategy = new GeneratePickupStrategy();

        EntityStorage entityStorage = new IdToEntityStorage(world);
        EntityFactory entityFactory = new EntityFactory.EntityFactoryBuilder(entityStorage)
            .register(EntityType.ZOMBIE, zombieGenerateStrategy)
            .register(EntityType.TRAIN, trainGenerateStrategy)
            .register(EntityType.PICKUP, pickupGenerateStrategy)
            .build();

        EntityBehaviourSystem entityBehaviourSystem = new EntityBehaviourSystem.EntityBehaviourSystemFactory(entityStorage, world)
            .register(EntityType.ZOMBIE, new ZombieBehaviour(player))
            .register(EntityType.BULLET, new BulletBehaviour())
            .register(EntityType.TRAIN, new TrainBehaviour(player))
            .build();

        // Chunk Generation
        GenerateChunkInputBoundary chunkGenerator = new GenerateChunkInteractor(blockRepository);
        PopulateChunkInputBoundary chunkPopulator = new PopulateChunkInteractor(entityFactory);
        RenderRadiusManagerInputBoundary renderRadiusManager = new RenderRadiusManagerInteractor(world);
        UpdateWorldInputBoundary updateWorld = new UpdateWorldInteractor(renderRadiusManager, chunkGenerator, chunkPopulator, world, player);

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

        EntityMeshSynchronizer meshSynchronizer = new EntityMeshSynchronizer(entityStorage, meshStorage);


        // TODO: invert this dependency, object renderer should be at the end
        // --- SETUP FRAMEWORKS ---
        ViewCamera camera = new ViewCamera();
        PickupInteractor pickupInteractor = new PickupInteractor(entityStorage, player);
        pickupController = new PickupController(player, pickupInteractor, mountEntity, dismountEntity, meshStorage);
        pickupInputAdapter = new PickUpInputAdapter(pickupController);

        ApplicationLifecyclePort lifecycleAdapter = new LibGDXLifecycleAdapter();
        ExitGameUseCase exitGameUseCase = new ExitGameUseCase(lifecycleAdapter);

        gameInputAdapter = new GameInputAdapter(playerMovementInteractor, exitGameUseCase, player);
        inventoryInputAdapter = new InventoryInputAdapter(player);


        // --- MESH + COL ---
        objectRenderer = new ObjectRenderer(camera, colHandler, meshStorage);
        cameraController = new FirstPersonCameraController(camera, player);

        // CHUNK + ENTITY RENDERING
        EntityRenderer entityRenderer = new EntityRenderer(entityStorage, meshFactory, meshStorage);
        ChunkRenderer chunkRenderer = new ChunkRenderer(objectRenderer, chunkMeshGenerator, entityRenderer);

        // --- PHYSICS ---

        worldSyncController = new WorldSyncController(
            RENDER_RADIUS,
            updateWorld,
            chunkRenderer
        );


        PhysicsControlPort physicsControlPort = new BulletPhysicsAdapter(meshStorage);
        gameSimulationController = new GameSimulationController(
            worldSyncController, colHandler, entityBehaviourSystem, meshSynchronizer, physicsControlPort, player
        );

        this.WinConditionInteractor = new WinConditionInteractor(world, player, exitGameUseCase);
        hud = new GameHUD(player, entityStorage, pickupController, exitGameUseCase);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hud.getUiStage());
        multiplexer.addProcessor(gameInputAdapter);
        multiplexer.addProcessor(inventoryInputAdapter);
        multiplexer.addProcessor(pickupInputAdapter);
        Gdx.input.setInputProcessor(multiplexer);
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void renderView() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        accumulator += deltaTime;

        // Theses should be polled at most once per frame
        inventoryInputAdapter.pollInput();
        pickupInputAdapter.pollInput();

        while (accumulator >= TIME_STEP) {
            accumulator -= TIME_STEP;
            cameraController.updatePrevious();

            // --- GAME LOGIC ---
            gameInputAdapter.processInput(TIME_STEP);

            gameSimulationController.update(TIME_STEP);

            WinConditionOutputData output = WinConditionInteractor.execute();
            if (output.isGameOver()) {
                hud.showEndGameDialog(output.getMessage());

            }
        }


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
