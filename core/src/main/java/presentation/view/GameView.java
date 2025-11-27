package presentation.view;

import application.use_cases.generate_entity.train.GenerateTrainStrategy;
import application.use_cases.generate_chunk.GenerateChunkInteractor;
import application.use_cases.generate_entity.zombie.GenerateZombieStrategy;
import domain.GamePosition;
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
import data_access.EntityStorage;
import data_access.InMemoryBlockRepository;
import domain.entities.*;
import domain.items.ItemTypes;
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
import net.mgsx.gltf.scene3d.scene.Scene;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

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

    // World rendering
//    private EntityRenderer entityRenderer;
//    private ChunkRenderer chunkRenderer;


    private WorldSyncController worldSyncController;

//    private BlockRepository blockRepository;
//    private BlockMaterialRepository materialRepository;
    private PickupStorage pickupStorage;

    private float accumulator;

//    private CollisionHandler colHandler;

//    private EntityBehaviourSystem entityBehaviourSystem;
    private GameSimulationController gameSimulationController;

    private GameHUD hud;

    // TODO: Merge with entity storage
    private final Map<WorldPickup, GameMesh> pickupMeshes = new HashMap<>();

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

        // TODO: Merge with entity storage
        this.pickupStorage = new PickupStorage();
        EntityStorage entityStorage = new IdToEntityStorage();
        EntityFactory entityFactory = new EntityFactory.EntityFactoryBuilder(entityStorage)
            .register(EntityType.ZOMBIE, zombieGenerateStrategy)
            .register(EntityType.TRAIN, trainGenerateStrategy)
            .build();



        PickupInteractor pickupInteractor = new PickupInteractor(pickupStorage);
        PickupController pickupController = new PickupController(player, pickupInteractor);

        gameInputAdapter = new GameInputAdapter(playerMovementInteractor, player);
        Gdx.input.setInputProcessor(gameInputAdapter);
        Gdx.input.setCursorCatched(true);

        inventoryInputAdapter = new InventoryInputAdapter(player);
        pickupInputAdapter = new PickUpInputAdapter(pickupController);


        // Chunk Generation
        GenerateChunkInteractor chunkGenerator = new GenerateChunkInteractor(blockRepository, entityFactory);
        RenderRadiusManagerInteractor renderRadiusManager = new RenderRadiusManagerInteractor(world);

        // --- WORLD RENDERING SYSTEM INITIALIZATION ---
        BlockMaterialRepository materialRepository = new TexturedBlockMaterialRepository();
        ChunkMeshGenerator chunkMeshGenerator = new ChunkMeshGenerator(world, blockRepository, (TexturedBlockMaterialRepository) materialRepository);
        GenerateZombieMeshStrategy zombieMeshStrategy = new GenerateZombieMeshStrategy();
        GenerateTrainMeshStrategy trainMeshStrategy = new GenerateTrainMeshStrategy();

        CollisionHandler colHandler = new CollisionHandler();

        MeshStorage meshStorage = new IdToMeshStorage(colHandler);
        MeshFactory meshFactory = new MeshFactory.MeshFactoryBuilder(meshStorage)
            .register(EntityType.ZOMBIE, zombieMeshStrategy)
            .register(EntityType.TRAIN, trainMeshStrategy)
            .build();

        // TODO: invert this dependency, object renderer should be at the end
        // --- SETUP FRAMEWORKS ---
        ViewCamera camera = new ViewCamera();
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
            chunkRenderer
        );

        gameSimulationController = new GameSimulationController(worldSyncController, colHandler, entityBehaviourSystem, world);
        hud = new GameHUD(player, pickupController);


        //TESTING
        pickupStorage.addPickup(new WorldPickup(ItemTypes.COAL, new GamePosition(5, 16, 0)));
        pickupStorage.addPickup(new WorldPickup(ItemTypes.WOOD_LOG, new GamePosition(15, 16, 0)));
        pickupStorage.addPickup(new WorldPickup(ItemTypes.OIL_BARREL, new GamePosition(25, 16, 0)));
        // TESTING === CREATE VISUAL + PHYSICS FOR EACH PICKUP ===
        for (WorldPickup pickup : pickupStorage.getAll()) {
            // Visual model from your factory
            Scene scene = ItemPickupSceneFactory.createSceneForPickup(pickup);

            // Position the scene at the pickup's location
            GamePosition pickupPos = pickup.getPosition();
            Matrix4 transform = new Matrix4().setToTranslation(pickupPos.x, pickupPos.y, pickupPos.z);
            scene.modelInstance.transform.set(transform);

            // Create a simple static collision body from the model's bounding box
            BoundingBox bbox = new BoundingBox();
            scene.modelInstance.calculateBoundingBox(bbox);

            Vector3 halfExtents = new Vector3();
            bbox.getDimensions(halfExtents).scl(0.5f); // full -> half extents

            btCollisionShape shape = new btBoxShape(halfExtents);

            btDefaultMotionState motionState = new btDefaultMotionState(transform);
            Vector3 inertia = new Vector3(0, 0, 0); // static body, no inertia
            btRigidBody.btRigidBodyConstructionInfo info =
                new btRigidBody.btRigidBodyConstructionInfo(
                    0f,           // mass = 0 => static
                    motionState,
                    shape,
                    inertia
                );
            btRigidBody body = new btRigidBody(info);
            info.dispose();

            // Wrap everything in a GameMesh
            int meshId = pickup.hashCode();
            GameMesh mesh = new GameMesh(meshId, scene, body, motionState);
            mesh.setStatic(true);

            // Register with renderer & your map
            objectRenderer.add(mesh);
            pickupMeshes.put(pickup, mesh);
        }

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
        syncPickupVisuals();
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

    //FOR TESTING (SUBJECT TO CHANGE)
    private void syncPickupVisuals() {
        Set<WorldPickup> current = new HashSet<>(pickupStorage.getAll());

        Iterator<Map.Entry<WorldPickup, GameMesh>> it = pickupMeshes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<WorldPickup, GameMesh> entry = it.next();
            WorldPickup pickup = entry.getKey();
            GameMesh mesh = entry.getValue();

            if (!current.contains(pickup)) {
                objectRenderer.remove(mesh);
                it.remove();
            }
        }
    }
}
