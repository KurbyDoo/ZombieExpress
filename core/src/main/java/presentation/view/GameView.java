package presentation.view;

import application.use_cases.generate_entity.zombie.GenerateZombieStrategy;
import application.use_cases.generate_mesh.GenerateZombieMeshStrategy;
import application.use_cases.ports.BlockRepository;
import application.use_cases.player_movement.PlayerMovementInputBoundary;
import application.use_cases.player_movement.PlayerMovementInteractor;
import data_access.EntityStorage;
import data_access.InMemoryBlockRepository;
import domain.entities.EntityFactory;
import domain.entities.EntityType;
import domain.entities.IdToEntityStorage;
import domain.player.Player;
import domain.World;
import physics.CollisionHandler;
import physics.GameMesh;
import physics.HitBox;
import infrastructure.rendering.*;
import presentation.controllers.CameraController;
import presentation.controllers.FirstPersonCameraController;
import infrastructure.input_boundary.GameInputAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
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

    private HitBox block;

    // Entity Management
//    private EntityController entityController;
//    private EntityGenerationInteractor entityGenerationInteractor;
//    private RenderZombieInteractor renderZombieInteractor;
//    private ZombieInstanceUpdater zombieInstanceUpdater;

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

        // --- ENTITY SYSTEM INITIALIZATION ---
//        GenerateChunkMeshStrategy chunkMeshStrategy = new GenerateChunkMeshStrategy(blockRepository, materialRepository, world);

        GenerateZombieStrategy zombieGenerateStrategy = new GenerateZombieStrategy();
        GenerateZombieMeshStrategy zombieMeshStrategy = new GenerateZombieMeshStrategy();

        EntityStorage entityStorage = new IdToEntityStorage();
        EntityFactory entityFactory = new EntityFactory.EntityFactoryBuilder(entityStorage)
            .register(EntityType.ZOMBIE, zombieGenerateStrategy)
            .build();

        MeshStorage meshStorage = new IdToMeshStorage();
        MeshFactory meshFactory = new MeshFactory.MeshFactoryBuilder(meshStorage)
            .register(EntityType.ZOMBIE, zombieMeshStrategy)
            .build();

        // --- MESH + COL ---
        colHandler = new CollisionHandler();
        objectRenderer = new ObjectRenderer(camera, colHandler, meshStorage);
        world = new World();

        // physics testing
        block = new HitBox("box", BOX, 1, 1, 1);
        GameMesh red = block.construct();
        red.getScene().modelInstance.transform.setToTranslation(10.5f, 100, 90.5f);
        red.body.setWorldTransform(red.getScene().modelInstance.transform);
        objectRenderer.add(red);

//        ZombieStorage zombieStorage = new ZombieStorage();
//        entityGenerationInteractor = new EntityGenerationInteractor(zombieStorage);
//        renderZombieInteractor = new RenderZombieInteractor(zombieStorage);
//        ZombieInstanceUpdater zombieInstanceUpdater = new ZombieInstanceUpdater(objectRenderer, zombieStorage);

        // Initial entity setup
        // The EntityController will use the ZombieInstanceUpdater to add/remove Scenes from the SceneManager
//        entityController = new EntityController(entityGenerationInteractor, renderZombieInteractor, zombieStorage, zombieInstanceUpdater);
//        entityController.generateZombie();


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
            worldSyncController.update();
        }

        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
//        entityController.renderZombie();
        objectRenderer.render(deltaTime);
    }


    @Override
    public void disposeView() {
        // Dispose world-related components first
        worldSyncController.dispose();

        objectRenderer.dispose();
        block.dispose();
    }
}
