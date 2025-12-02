package presentation.view;

import application.game_use_cases.generate_entity.bullet.GenerateBulletStrategy;
import application.game_use_cases.shoot.ShootInteractor;
import application.game_use_cases.update_entity.BulletBehaviour;
import application.game_use_cases.update_entity.TrainBehaviour;
import application.game_use_cases.update_entity.ZombieBehaviour;
import application.game_use_cases.win_condition.WinConditionInputBoundary;
import application.game_use_cases.win_condition.WinConditionInteractor;
import application.game_use_cases.dismount_entity.DismountEntityInputBoundary;
import application.game_use_cases.dismount_entity.DismountEntityInteractor;
import application.game_use_cases.exit_game.ExitGameUseCase;
import application.game_use_cases.generate_chunk.GenerateChunkInputBoundary;
import application.game_use_cases.generate_entity.pickup.GeneratePickupStrategy;
import application.game_use_cases.generate_entity.train.GenerateTrainStrategy;
import application.game_use_cases.generate_chunk.GenerateChunkInteractor;
import application.game_use_cases.generate_entity.zombie.GenerateZombieStrategy;
import application.game_use_cases.mount_entity.MountEntityInputBoundary;
import application.game_use_cases.mount_entity.MountEntityInteractor;
import application.game_use_cases.update_world.UpdateWorldInputBoundary;
import application.game_use_cases.update_world.UpdateWorldInteractor;
import application.game_use_cases.populate_chunk.PopulateChunkInteractor;
import application.game_use_cases.populate_chunk.PopulateChunkInputBoundary;
import application.game_use_cases.ports.ApplicationLifecyclePort;
import application.game_use_cases.win_condition.WinConditionOutputData;
import application.interface_use_cases.player_data.SavePlayerDataInputData;
import application.interface_use_cases.player_data.SavePlayerDataInteractor;
import data_access.IdToEntityStorage;
import domain.player.PlayerSession;
import domain.GamePosition;
import infrastructure.rendering.strategies.GenerateBulletMeshStrategy;
import infrastructure.rendering.strategies.GeneratePickupMeshStrategy;
import infrastructure.rendering.strategies.GenerateTrainMeshStrategy;
import infrastructure.rendering.strategies.GenerateZombieMeshStrategy;
import domain.repositories.BlockRepository;
import application.game_use_cases.item_interaction.PickupInteractor;
import application.game_use_cases.item_interaction.FuelTrainInteractor;
import application.game_use_cases.player_movement.PlayerMovementInputBoundary;
import application.game_use_cases.player_movement.PlayerMovementInteractor;
import application.game_use_cases.ports.PhysicsControlPort;
import application.game_use_cases.render_radius.RenderRadiusManagerInputBoundary;
import application.game_use_cases.render_radius.RenderRadiusManagerInteractor;
import application.game_use_cases.update_entity.EntityBehaviourSystem;
import data_access.InMemoryBlockRepository;
import domain.entities.*;
import domain.player.Player;
import domain.World;
import application.game_use_cases.query_camera_data.PlayerCameraDataQuery;
import physics.BulletPhysicsAdapter;
import physics.CollisionHandler;
import presentation.controllers.*;
import presentation.view.hud.GameHUD;
import infrastructure.rendering.*;
import infrastructure.input_boundary.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

public class GameView implements Viewable{
    private Player player;
    private PlayerSession playerSession;
    private SavePlayerDataInteractor saveScore;
    private boolean winConditionCheckedOnce = false;

    public GameView(PlayerSession playerSession, SavePlayerDataInteractor saveScore) {
        this.playerSession = playerSession;
        this.saveScore = saveScore;
    }


    private boolean initialized = false;

    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;
    private final int RENDER_RADIUS = 6; // The radius in chunks where meshes are visible

    public ObjectRenderer objectRenderer;
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

    private GameHUD hud;

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

        IdToEntityStorage entityStorage = new IdToEntityStorage(world);
        EntityFactory entityFactory = new EntityFactory.EntityFactoryBuilder(entityStorage)
            .register(EntityType.ZOMBIE, zombieGenerateStrategy)
            .register(EntityType.TRAIN, trainGenerateStrategy)
            .register(EntityType.BULLET, bulletGenerateStrategy)
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
        GenerateBulletMeshStrategy bulletMeshStrategy = new GenerateBulletMeshStrategy();
        GeneratePickupMeshStrategy pickupMeshStrategy = new GeneratePickupMeshStrategy();

        CollisionHandler colHandler = new CollisionHandler();

        MeshStorage meshStorage = new IdToMeshStorage(colHandler);
        MeshFactory meshFactory = new MeshFactory.MeshFactoryBuilder(meshStorage)
            .register(EntityType.ZOMBIE, zombieMeshStrategy)
            .register(EntityType.TRAIN, trainMeshStrategy)
            .register(EntityType.BULLET, bulletMeshStrategy)
            .register(EntityType.PICKUP, pickupMeshStrategy)
            .build();

        EntityMeshSynchronizer meshSynchronizer = new EntityMeshSynchronizer(entityStorage, meshStorage);


        // TODO: invert this dependency, object renderer should be at the end
        // --- SETUP FRAMEWORKS ---
        ViewCamera camera = new ViewCamera();
        PickupInteractor pickupInteractor = new PickupInteractor(entityStorage, player);
        FuelTrainInteractor fuelTrainInteractor = new FuelTrainInteractor(entityStorage, player);
        itemInteractionController = new ItemInteractionController(player, pickupInteractor, fuelTrainInteractor, mountEntity, dismountEntity, meshStorage);
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

        // Shoot
        ShootInteractor shootInteractor = new ShootInteractor(entityFactory);
        shootController = new ShootController(player, shootInteractor, meshStorage, entityRenderer);
        shootInputAdapter = new ShootInputAdapter(player, shootController);

        this.WinConditionInteractor = new WinConditionInteractor(world, player, entityStorage, exitGameUseCase);
        hud = new GameHUD(player, entityStorage, itemInteractionController, exitGameUseCase);

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
                playerSession.setHeightScore(Math.max(playerSession.getHeightScore(),score));
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
    public void saveOnExit(){
        int score = player.getScore();

        System.out.println("[GameView] Manual Exit → Saving score: " + score);
        if (playerSession == null){
            System.out.println("[GameView] PlayerSession NULL, cannot save score to the fire store");
            return;
        }

        playerSession.setLastScore(score);
        playerSession.setHeightScore(Math.max(playerSession.getHeightScore(), score));
        saveScore.execute(new SavePlayerDataInputData(score));
    }

    @Override
    public void disposeView() {
        if (!winConditionCheckedOnce){
            int score = player.getScore();
            System.out.println("[GameView] Manual Exit → Saving score: " + score);

            if (playerSession != null){
                playerSession.setLastScore(score);
                playerSession.setHeightScore(Math.max(playerSession.getHeightScore(), score));
                saveScore.execute(new SavePlayerDataInputData(score));
            }else {
                System.out.println("[GameView] PlayerSession NULL, cannot save score");
            }

        }
        // Dispose world-related components first
        worldSyncController.dispose();

        objectRenderer.dispose();
    }
}
