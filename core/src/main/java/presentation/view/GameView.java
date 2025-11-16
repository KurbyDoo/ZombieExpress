package presentation.view;

import application.use_cases.EntityGeneration.EntityGenerationInteractor;
import application.use_cases.RenderZombie.RenderZombieInteractor;
import application.use_cases.ports.BlockRepository;
import application.use_cases.ports.BlockMaterialRepository; // Added from Left
import application.use_cases.ChunkRadius.ChunkRadiusManagerInteractor; // Added from Left
import application.use_cases.chunk_generation.ChunkGenerationInteractor; // Added from Left (and inferred)
import data_access.InMemoryBlockRepository;
import domain.entities.Player;
import domain.entities.World;
import domain.entities.ZombieStorage;
import physics.CollisionHandler;
import physics.GameMesh;
import physics.HitBox;
import presentation.ZombieInstanceUpdater;
import infrastructure.rendering.*; // Covers ObjectRenderer, ModelGeneratorFacade, LibGDXMaterialRepository, ChunkRenderer
import presentation.controllers.CameraController;
import presentation.controllers.EntityController;
import presentation.controllers.FirstPersonCameraController;
import infrastructure.input_boundary.GameInputAdapter;
import application.use_cases.player_movement.PlayerMovementInputBoundary;
import application.use_cases.player_movement.PlayerMovementInteractor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import presentation.controllers.WorldGenerationController;
import io.github.testlibgdx.ChunkLoader; // Keep from previous versions if needed for background processing

import application.use_cases.ChunkRadius.*;
import infrastructure.rendering.ChunkRenderer;

import static physics.HitBox.ShapeTypes.SPHERE;

public class GameView implements Viewable{
    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;

    public ObjectRenderer objectRenderer;
    // Uses ModelGeneratorFacade from Left/Middle, not GameMeshBuilder from Right
    public ModelGeneratorFacade meshBuilder;
    public World world;
    private CameraController cameraController;
    private GameInputAdapter gameInputAdapter;
    private ViewCamera camera;
    private ChunkLoader chunkLoader;
    private WorldGenerationController worldGenerationController;
    private Player player;

    // Chunk Management fields (From Left)
    private ChunkRenderer chunkRenderer;
    private ChunkRadiusManagerInteractor chunkRadiusManager;
    private BlockRepository blockRepository;
    private BlockMaterialRepository materialRepository;
    private ChunkGenerationInteractor chunkGenerationUseCase; // Placeholder needed for ChunkRadiusManager

    private float accumulator;

    // Physics fields (From Left/Middle)
    private CollisionHandler colHandler;
    private HitBox block;

    // Entity fields (Consistent across Left/Middle/Right)
    private EntityController entityController;
    private EntityGenerationInteractor entityGenerationInteractor;
    private RenderZombieInteractor renderZombieInteractor;


    private ChunkRenderer chunkRenderer;
    private ChunkRadiusManagerInteractor chunkRadiusManager;
    private BlockRepository blockRepository;
    private BlockMaterialRepository materialRepository;
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
        meshBuilder = new ModelGeneratorFacade(world, blockRepository, materialRepository);
        chunkLoader = new ChunkLoader(meshBuilder, objectRenderer);

        worldGenerationController = new WorldGenerationController(world, chunkLoader, blockRepository);
        worldGenerationController.generateInitialWorld(8, 4, 32);
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
        // NEW system for derendering/radius-based loading:
        chunkRenderer = new ChunkRenderer(meshBuilder, objectRenderer, world);

    // Radius manager ties domain + generator + renderer together:
        chunkRadiusManager = new ChunkRadiusManagerInteractor(
            world,
            chunkGenerationUseCase,   // generator you already have
            chunkRenderer,            // renderer output boundary
            4                        // radius
        );


//        worldGenerationController = new WorldGenerationController(chunkGenerationUseCase, world, chunkLoader);

//        worldGenerationController.generateInitialWorld(8, 4, 32);
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

            // Update chunks based on player position:
            chunkRadiusManager.execute(player.getPosition());
            chunkRenderer.render();

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
