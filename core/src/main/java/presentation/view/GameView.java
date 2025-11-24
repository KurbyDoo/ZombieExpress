package presentation.view;

import application.use_cases.generate_entity.train.GenerateTrainStrategy;
import application.use_cases.generate_entity.zombie.GenerateZombieStrategy;
import application.use_cases.generate_mesh.GenerateTrainMeshStrategy;
import application.use_cases.generate_mesh.GenerateZombieMeshStrategy;
import application.use_cases.ports.BlockRepository;
import application.use_cases.player_movement.PlayerMovementInputBoundary;
import application.use_cases.player_movement.PlayerMovementInteractor;
import application.use_cases.ports.PhysicsControlPort;
import application.use_cases.update_entity.EntityBehaviourSystem;
import data_access.EntityStorage;
import data_access.InMemoryBlockRepository;
import domain.entities.EntityFactory;
import domain.entities.EntityType;
import domain.entities.IdToEntityStorage;
import domain.player.Player;
import domain.World;
import physics.BulletPhysicsAdapter;
import physics.CollisionHandler;
import physics.GameMesh;
import physics.HitBox;
import infrastructure.rendering.*;
import presentation.controllers.CameraController;
import presentation.controllers.FirstPersonCameraController;
import infrastructure.input_boundary.GameInputAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import presentation.controllers.GameSimulationController;
import presentation.controllers.WorldSyncController;

import static physics.HitBox.ShapeTypes.BOX;

public class GameView implements Viewable{
    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;
    private final int RENDER_RADIUS = 6; // The radius in chunks where meshes are visible

    public ObjectRenderer objectRenderer;
    public World world;
    private CameraController cameraController;
    private GameInputAdapter gameInputAdapter;
    private ViewCamera camera;

    private Player player;

    private WorldSyncController worldSyncController;

    private BlockRepository blockRepository;
    private BlockMaterialRepository materialRepository;

    private float accumulator;

    private CollisionHandler colHandler;

    private EntityBehaviourSystem entityBehaviourSystem;

    private GameSimulationController gameSimulationController;


    @Override
    public void createView() {
        Vector3 startingPosition = new Vector3(0, 2f, 0);
        player = new Player(startingPosition);

        camera = new ViewCamera();

        PlayerMovementInputBoundary playerMovementInteractor = new PlayerMovementInteractor(player);

        gameInputAdapter = new GameInputAdapter(playerMovementInteractor);
        Gdx.input.setInputProcessor(gameInputAdapter);
        Gdx.input.setCursorCatched(true);

        cameraController = new FirstPersonCameraController(camera, player);

        blockRepository = new InMemoryBlockRepository();
        materialRepository = new LibGDXMaterialRepository();

        // --- ENTITY SYSTEM INITIALIZATION ---
        colHandler = new CollisionHandler();

        GenerateZombieStrategy zombieGenerateStrategy = new GenerateZombieStrategy();
        GenerateZombieMeshStrategy zombieMeshStrategy = new GenerateZombieMeshStrategy();

        GenerateTrainStrategy trainGenerateStrategy = new GenerateTrainStrategy();
        GenerateTrainMeshStrategy trainMeshStrategy = new GenerateTrainMeshStrategy();

        EntityStorage entityStorage = new IdToEntityStorage();
        EntityFactory entityFactory = new EntityFactory.EntityFactoryBuilder(entityStorage)
            .register(EntityType.ZOMBIE, zombieGenerateStrategy)
            .register(EntityType.TRAIN, trainGenerateStrategy)
            .build();

        MeshStorage meshStorage = new IdToMeshStorage(colHandler);
        MeshFactory meshFactory = new MeshFactory.MeshFactoryBuilder(meshStorage)
            .register(EntityType.ZOMBIE, zombieMeshStrategy)
            .register(EntityType.TRAIN, trainMeshStrategy)
            .build();

        // --- MESH + COL ---
        objectRenderer = new ObjectRenderer(camera, colHandler, meshStorage);
        world = new World();

        // --- PHYSICS ---
        PhysicsControlPort physicsAdapter = new BulletPhysicsAdapter(meshStorage);
        entityBehaviourSystem = new EntityBehaviourSystem(physicsAdapter, player, entityStorage, world);

        // --- CHUNK SYSTEM INITIALIZATION ---
        worldSyncController = new WorldSyncController(
            objectRenderer,
            world,
            player,
            entityFactory,
            entityStorage,
            meshFactory,
            meshStorage,
            blockRepository,
            materialRepository,
            RENDER_RADIUS
        );

        gameSimulationController = new GameSimulationController(worldSyncController, colHandler, entityBehaviourSystem, world);
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

            gameSimulationController.update(TIME_STEP);
        }

        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
        objectRenderer.render(deltaTime);
    }


    @Override
    public void disposeView() {
        // Dispose world-related components first
        worldSyncController.dispose();

        objectRenderer.dispose();
    }
}
