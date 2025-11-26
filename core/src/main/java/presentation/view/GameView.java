package presentation.view;

import application.use_cases.close_game.CloseGameInputBoundary;
import application.use_cases.close_game.CloseGameInteractor;
import application.use_cases.generate_entity.train.GenerateTrainStrategy;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputBoundary;
import application.use_cases.chunk_mesh_generation.ChunkTexturedMeshGeneration;
import application.use_cases.generate_chunk.GenerateChunkInteractor;
import application.use_cases.generate_entity.zombie.GenerateZombieStrategy;
import application.use_cases.generate_mesh.GenerateTrainMeshStrategy;
import application.use_cases.generate_mesh.GenerateZombieMeshStrategy;
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
import physics.GameMesh;
import physics.HitBox;
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

    public ObjectRenderer objectRenderer;
    public World world;
    private CameraController cameraController;
    private GameInputAdapter gameInputAdapter;
    private InventoryInputAdapter inventoryInputAdapter;
    private PickUpInputAdapter  pickupInputAdapter;
    private ViewCamera camera;

    private Player player;

    private GenerateChunkInteractor chunkGenerator;
    private ChunkMeshGenerationInputBoundary chunkMeshGenerator;
    private WorldSyncController worldSyncController;

    private BlockRepository blockRepository;
    private BlockMaterialRepository materialRepository;
    private final PickupStorage pickupStorage = new PickupStorage();

    private float accumulator;

    private CollisionHandler colHandler;

    private EntityBehaviourSystem entityBehaviourSystem;
    private GameSimulationController gameSimulationController;

    private CloseGameInputBoundary closeGameInteractor;


    private GameHUD hud;

    private final Map<WorldPickup, GameMesh> pickupMeshes = new HashMap<>();

    @Override
    public void createView() {
        Vector3 startingPosition = new Vector3(0, 3f, 0);
        player = new Player(startingPosition);

        camera = new ViewCamera();

        PlayerMovementInputBoundary playerMovementInteractor = new PlayerMovementInteractor(player);

        closeGameInteractor = new CloseGameInteractor();

        //TESTING
        pickupStorage.addPickup(new WorldPickup(ItemTypes.COAL, new Vector3(5, 16, 0)));
        pickupStorage.addPickup(new WorldPickup(ItemTypes.WOOD_LOG, new Vector3(15, 16, 0)));
        pickupStorage.addPickup(new WorldPickup(ItemTypes.OIL_BARREL, new Vector3(25, 16, 0)));

        PickupInteractor pickupInteractor = new PickupInteractor(pickupStorage);
        PickupController pickupController = new PickupController(player, pickupInteractor);

        gameInputAdapter = new GameInputAdapter(playerMovementInteractor, closeGameInteractor, player);
        Gdx.input.setInputProcessor(gameInputAdapter);
        Gdx.input.setCursorCatched(true);

        inventoryInputAdapter = new InventoryInputAdapter(player);
        pickupInputAdapter = new PickUpInputAdapter(pickupController);

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
        this.chunkGenerator = new GenerateChunkInteractor(blockRepository, entityFactory);
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
            chunkMeshGenerator,
            RENDER_RADIUS
        );

        gameSimulationController = new GameSimulationController(worldSyncController, colHandler, entityBehaviourSystem, world);
        hud = new GameHUD(player, pickupController);

        // TESTING === CREATE VISUAL + PHYSICS FOR EACH PICKUP ===
        for (WorldPickup pickup : pickupStorage.getAll()) {
            // Visual model from your factory
            Scene scene = ItemPickupSceneFactory.createSceneForPickup(pickup);

            // Position the scene at the pickup's location
            Matrix4 transform = new Matrix4().setToTranslation(pickup.getPosition());
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

        player.updatePassiveHealing(deltaTime);

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
