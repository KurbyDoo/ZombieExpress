package framework.view;

import application.account_features.player_data.SavePlayerDataInputData;
import application.account_features.player_data.SavePlayerDataInteractor;
import application.game_features.dismount_entity.DismountEntityInputBoundary;
import application.game_features.dismount_entity.DismountEntityInteractor;
import application.game_features.exit_game.ExitGameUseCase;
import application.game_features.generate_chunk.GenerateChunkInputBoundary;
import application.game_features.generate_chunk.GenerateChunkInteractor;
import application.game_features.generate_entity.bullet.GenerateBulletStrategy;
import application.game_features.generate_entity.pickup.GeneratePickupStrategy;
import application.game_features.generate_entity.player_entity.GeneratePlayerEntityInputData;
import application.game_features.generate_entity.player_entity.GeneratePlayerEntityStrategy;
import application.game_features.generate_entity.train.GenerateTrainStrategy;
import application.game_features.generate_entity.zombie.GenerateZombieStrategy;
import application.game_features.item_interaction.ItemInteractionInteractor;
import application.game_features.mount_entity.MountEntityInputBoundary;
import application.game_features.mount_entity.MountEntityInteractor;
import application.game_features.player_movement.PlayerMovementInputBoundary;
import application.game_features.player_movement.PlayerMovementInteractor;
import application.game_features.populate_chunk.PopulateChunkInputBoundary;
import application.game_features.populate_chunk.PopulateChunkInteractor;
import application.game_features.query_camera_data.PlayerCameraDataQuery;
import application.game_features.remove_entity.RemoveEntityInputData;
import application.game_features.remove_entity.RemoveEntityInteractor;
import application.game_features.render_radius.RenderRadiusManagerInputBoundary;
import application.game_features.render_radius.RenderRadiusManagerInteractor;
import application.game_features.shoot.ShootInteractor;
import application.game_features.update_entity.BulletBehaviour;
import application.game_features.update_entity.EntityBehaviourSystem;
import application.game_features.update_entity.PlayerEntityBehaviour;
import application.game_features.update_entity.TrainBehaviour;
import application.game_features.update_entity.ZombieBehaviour;
import application.game_features.update_world.UpdateWorldInputBoundary;
import application.game_features.update_world.UpdateWorldInteractor;
import application.game_features.win_condition.WinConditionInputBoundary;
import application.game_features.win_condition.WinConditionInteractor;
import application.game_features.win_condition.WinConditionOutputData;
import application.gateways.BlockRepository;
import application.ports.ApplicationLifecyclePort;
import application.ports.PhysicsControlPort;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import domain.entities.EntityFactory;
import domain.entities.EntityType;
import domain.player.Player;
import domain.player.PlayerSession;
import domain.world.GamePosition;
import domain.world.World;
import framework.data_access.IdToEntityStorage;
import framework.data_access.InMemoryBlockRepository;
import framework.physics.CollisionHandler;
import framework.physics.EntityContactFacade;
import framework.rendering.BlockMaterialRepository;
import framework.rendering.ChunkMeshGenerator;
import framework.rendering.ChunkRenderer;
import framework.rendering.EntityMeshSynchronizer;
import framework.rendering.EntityRenderer;
import framework.rendering.IdToMeshStorage;
import framework.rendering.MeshFactory;
import framework.rendering.MeshStorage;
import framework.rendering.ObjectRenderer;
import framework.rendering.TexturedBlockMaterialRepository;
import framework.rendering.strategies.GenerateBulletMeshStrategy;
import framework.rendering.strategies.GeneratePickupMeshStrategy;
import framework.rendering.strategies.GeneratePlayerEntityMeshStrategy;
import framework.rendering.strategies.GenerateTrainMeshStrategy;
import framework.rendering.strategies.GenerateZombieMeshStrategy;
import framework.view.hud.GameHUD;
import interface_adapter.controllers.CameraController;
import interface_adapter.controllers.EntityCleanupController;
import interface_adapter.controllers.GameSimulationController;
import interface_adapter.controllers.ItemInteractionController;
import interface_adapter.controllers.ShootController;
import interface_adapter.controllers.WorldSyncController;
import interface_adapter.input.GameInputAdapter;
import interface_adapter.input.InventoryInputAdapter;
import interface_adapter.input.ItemInteractionInputAdapter;
import interface_adapter.input.LibGDXLifecycleAdapter;
import interface_adapter.input.ShootInputAdapter;
import interface_adapter.physics.BulletPhysicsAdapter;
import java.util.List;

public class GameView implements Viewable {
    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;
    private final int RENDER_RADIUS = 6; // The radius in chunks where meshes are visible
    public ObjectRenderer objectRenderer;
    private Player player;
    private PlayerSession playerSession;
    private SavePlayerDataInteractor saveScore;
    private boolean winConditionCheckedOnce = false;
    private boolean initialized = false;
    private CameraController cameraController;
    // TODO: These should be merged
    private GameInputAdapter gameInputAdapter;
    private InventoryInputAdapter inventoryInputAdapter;
    private ItemInteractionInputAdapter pickupInputAdapter;
    private ShootInputAdapter shootInputAdapter;
    private WorldSyncController worldSyncController;
    private float accumulator;
    private GameSimulationController gameSimulationController;
    private ItemInteractionController itemInteractionController;
    private ShootController shootController;
    private WinConditionInputBoundary WinConditionInteractor;
    private EntityCleanupController cleanupController;
    private GameHUD hud;
    private EntityContactFacade contactFacade;
    private List<Integer> pendingRemoval;
    private RemoveEntityInteractor removeEntityInteractor;
    private RemoveEntityInputData removeEntityInputData;
    private GeneratePlayerEntityInputData playerEntityInputData;

    public GameView(PlayerSession playerSession, SavePlayerDataInteractor saveScore) {
        this.playerSession = playerSession;
        this.saveScore = saveScore;
    }

    @Override
    public void createView() {
        // ---  WORLD GENERATION SYSTEM INITIALIZATION ---
        World world = new World();
        BlockRepository blockRepository = new InMemoryBlockRepository();

        GamePosition startingPosition = new GamePosition(0, 3f, 0);
        this.player = new Player(startingPosition);
        MountEntityInputBoundary mountEntity = new MountEntityInteractor(player);
        DismountEntityInputBoundary dismountEntity = new DismountEntityInteractor(player);

        PlayerMovementInputBoundary playerMovementInteractor = new PlayerMovementInteractor(player);

        // Entity Generation
        GenerateZombieStrategy zombieGenerateStrategy = new GenerateZombieStrategy();
        GenerateTrainStrategy trainGenerateStrategy = new GenerateTrainStrategy();
        GenerateBulletStrategy bulletGenerateStrategy = new GenerateBulletStrategy();
        GeneratePickupStrategy pickupGenerateStrategy = new GeneratePickupStrategy();
        GeneratePlayerEntityStrategy playerEntityStrategy = new GeneratePlayerEntityStrategy();

        IdToEntityStorage entityStorage = new IdToEntityStorage(world);
        EntityFactory entityFactory = new EntityFactory.EntityFactoryBuilder(entityStorage)
            .register(EntityType.ZOMBIE, zombieGenerateStrategy)
            .register(EntityType.TRAIN, trainGenerateStrategy)
            .register(EntityType.BULLET, bulletGenerateStrategy)
            .register(EntityType.PICKUP, pickupGenerateStrategy)
            .register(EntityType.PLAYER, playerEntityStrategy)
            .build();


        EntityBehaviourSystem entityBehaviourSystem =
            new EntityBehaviourSystem.EntityBehaviourSystemFactory(entityStorage, world)
                .register(EntityType.ZOMBIE, new ZombieBehaviour(player))
                .register(EntityType.BULLET, new BulletBehaviour())
                .register(EntityType.TRAIN, new TrainBehaviour(player))
                .register(EntityType.PLAYER, new PlayerEntityBehaviour(player))
                .build();


        // Chunk Generation
        GenerateChunkInputBoundary chunkGenerator = new GenerateChunkInteractor(blockRepository);
        PopulateChunkInputBoundary chunkPopulator = new PopulateChunkInteractor(entityFactory);
        RenderRadiusManagerInputBoundary renderRadiusManager = new RenderRadiusManagerInteractor(world);
        UpdateWorldInputBoundary updateWorld =
            new UpdateWorldInteractor(renderRadiusManager, chunkGenerator, chunkPopulator, world, player);

        // --- WORLD RENDERING SYSTEM INITIALIZATION ---
        BlockMaterialRepository materialRepository = new TexturedBlockMaterialRepository();
        ChunkMeshGenerator chunkMeshGenerator =
            new ChunkMeshGenerator(world, blockRepository, (TexturedBlockMaterialRepository) materialRepository);
        GenerateZombieMeshStrategy zombieMeshStrategy = new GenerateZombieMeshStrategy();
        GenerateTrainMeshStrategy trainMeshStrategy = new GenerateTrainMeshStrategy();
        GenerateBulletMeshStrategy bulletMeshStrategy = new GenerateBulletMeshStrategy();
        GeneratePickupMeshStrategy pickupMeshStrategy = new GeneratePickupMeshStrategy();
        GeneratePlayerEntityMeshStrategy playerEntityMeshStrategy = new GeneratePlayerEntityMeshStrategy();

        contactFacade = new EntityContactFacade(entityStorage);     // for usecases
        CollisionHandler colHandler = new CollisionHandler(contactFacade);

        MeshStorage meshStorage = new IdToMeshStorage(colHandler);
        MeshFactory meshFactory = new MeshFactory.MeshFactoryBuilder(meshStorage)
            .register(EntityType.ZOMBIE, zombieMeshStrategy)
            .register(EntityType.TRAIN, trainMeshStrategy)
            .register(EntityType.BULLET, bulletMeshStrategy)
            .register(EntityType.PICKUP, pickupMeshStrategy)
            .register(EntityType.PLAYER, playerEntityMeshStrategy)
            .build();


        EntityMeshSynchronizer meshSynchronizer = new EntityMeshSynchronizer(entityStorage, meshStorage);
        // ENTITY REMOVAL
        removeEntityInputData = new RemoveEntityInputData(entityStorage, meshStorage, pendingRemoval);

        // TODO: invert this dependency, object renderer should be at the end
        // --- SETUP FRAMEWORKS ---
        ViewCamera camera = new ViewCamera();
        ItemInteractionInteractor itemInteractionInteractor =
            new ItemInteractionInteractor(entityStorage, player, mountEntity, dismountEntity);
        itemInteractionController = new ItemInteractionController(itemInteractionInteractor, meshStorage);
        pickupInputAdapter = new ItemInteractionInputAdapter(itemInteractionController);

        ApplicationLifecyclePort lifecycleAdapter = new LibGDXLifecycleAdapter();
        ExitGameUseCase exitGameUseCase = new ExitGameUseCase(lifecycleAdapter);
        gameInputAdapter = new GameInputAdapter(playerMovementInteractor, exitGameUseCase, player);
        inventoryInputAdapter = new InventoryInputAdapter(player);
        PlayerCameraDataQuery playerCameraDataQuery = new PlayerCameraDataQuery(player);


        // --- MESH + COL ---
        objectRenderer = new ObjectRenderer(camera, colHandler, meshStorage);
        cameraController = new CameraController(camera, playerCameraDataQuery);

        // CHUNK + ENTITY RENDERING
        EntityRenderer entityRenderer = new EntityRenderer(entityStorage, meshFactory, meshStorage);
        ChunkRenderer chunkRenderer = new ChunkRenderer(objectRenderer, chunkMeshGenerator, entityRenderer);
        cleanupController = new EntityCleanupController(removeEntityInputData);

        // --- PHYSICS ---

        worldSyncController = new WorldSyncController(
            RENDER_RADIUS,
            updateWorld,
            chunkRenderer
        );

        PhysicsControlPort physicsControlPort = new BulletPhysicsAdapter(meshStorage);
        gameSimulationController = new GameSimulationController(
            worldSyncController, colHandler, entityBehaviourSystem, meshSynchronizer, physicsControlPort, player,
            cleanupController
        );

        // Shoot
        ShootInteractor shootInteractor = new ShootInteractor(entityFactory);
        shootController = new ShootController(player, shootInteractor, meshStorage, entityRenderer);
        shootInputAdapter = new ShootInputAdapter(player, shootController);

        this.WinConditionInteractor = new WinConditionInteractor(world, player, entityStorage, exitGameUseCase);
        hud = new GameHUD(player, entityStorage, itemInteractionController, exitGameUseCase);

//        gameSimulationController.update(TIME_STEP);

//        GeneratePlayerEntityInputData playerGenData = new GeneratePlayerEntityInputData(player.getPosition());

//        int playerID = entityFactory.create(playerGenData);

//        Entity playerEntity = entityStorage.getEntityByID(playerID);
//        GenerateMeshInputData meshInputData = new GenerateMeshInputData(playerEntity, playerID);
//        meshFactory.createMesh(meshInputData);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hud.getUiStage());
        multiplexer.addProcessor(gameInputAdapter);
        multiplexer.addProcessor(inventoryInputAdapter);
        multiplexer.addProcessor(pickupInputAdapter);
        multiplexer.addProcessor(shootInputAdapter);
        Gdx.input.setInputProcessor(multiplexer);
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void renderView() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        player.addTime(deltaTime);
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
                int score = output.getScore();
                System.out.println("[GameView] Final score from WinCondition = " + score);
                System.out.println("[GameView] PlayerSession BEFORE: last="
                    + playerSession.getLastScore()
                    + " top=" + playerSession.getHeightScore());

                playerSession.setLastScore(score);
                playerSession.setHeightScore(Math.max(playerSession.getHeightScore(), score));
                System.out.println("[GameView] PlayerSession AFTER: last="
                    + playerSession.getLastScore()
                    + " top=" + playerSession.getHeightScore());

                saveScore.execute(new SavePlayerDataInputData(score));

                System.out.println("[GameView] SaveScore interactor CALLED.");

                hud.showEndGameDialog(output.getMessage());
                // TODO: add store score logic here!!

            }
        }


        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
        itemInteractionController.refreshTarget();
        objectRenderer.render(deltaTime);

        // HUD
        hud.update(deltaTime);
        hud.render();
    }

    public void saveOnExit() {
        int score = player.getScore();

        System.out.println("[GameView] Manual Exit → Saving score: " + score);
        if (playerSession == null) {
            System.out.println("[GameView] PlayerSession NULL, cannot save score to the fire store");
            return;
        }

        playerSession.setLastScore(score);
        playerSession.setHeightScore(Math.max(playerSession.getHeightScore(), score));
        saveScore.execute(new SavePlayerDataInputData(score));
    }

    @Override
    public void disposeView() {
        if (!winConditionCheckedOnce) {
            int score = player.getScore();
            System.out.println("[GameView] Manual Exit → Saving score: " + score);

            if (playerSession != null) {
                playerSession.setLastScore(score);
                playerSession.setHeightScore(Math.max(playerSession.getHeightScore(), score));
                saveScore.execute(new SavePlayerDataInputData(score));
            } else {
                System.out.println("[GameView] PlayerSession NULL, cannot save score");
            }

        }
        // Dispose world-related components first
        worldSyncController.dispose();

        objectRenderer.dispose();
    }
}
