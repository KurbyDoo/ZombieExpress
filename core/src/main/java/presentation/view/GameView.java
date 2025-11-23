package presentation.view;

import application.use_cases.generate_entity.zombie.GenerateZombieStrategy;
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
import domain.entities.Player;
import domain.entities.World;
import domain.entities.Zombie; // delete this later
import presentation.controllers.CameraController;
import presentation.controllers.FirstPersonCameraController;
import infrastructure.input_boundary.GameInputAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import io.github.testlibgdx.ChunkLoader;
import infrastructure.rendering.GameMeshBuilder;
import infrastructure.rendering.ObjectRenderer;
import presentation.controllers.WorldGenerationController;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import domain.entities.InventorySlot;
import presentation.controllers.GameSimulationController;
import presentation.controllers.WorldSyncController;

import static physics.HitBox.ShapeTypes.BOX;
import com.badlogic.gdx.math.Vector3;
import application.use_cases.EntityGeneration.EntityGenerationInteractor;
import application.use_cases.RenderZombie.RenderZombieInteractor;
import application.use_cases.player_movement.PlayerMovementInputBoundary;
import application.use_cases.player_movement.PlayerMovementInteractor;
import application.use_cases.pickup.PickupInteractor;
import application.use_cases.ports.BlockRepository;
import data_access.InMemoryBlockRepository;
import domain.entities.*;
import net.mgsx.gltf.scene3d.scene.Scene;
import infrastructure.input_boundary.*;
import infrastructure.rendering.*;
import physics.CollisionHandler;
import physics.GameMesh;
import physics.HitBox;
import presentation.ZombieInstanceUpdater;
import presentation.controllers.*;
import presentation.view.hud.GameHUD;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import static physics.HitBox.ShapeTypes.BOX;
import static physics.HitBox.ShapeTypes.SPHERE;

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

    private WorldSyncController worldSyncController;

    private BlockRepository blockRepository;
    private BlockMaterialRepository materialRepository;
    private final PickupStorage pickupStorage = new PickupStorage();

    private float accumulator;

    private GameHUD hud;
    private float testDamageTimer = 0f;
    private GameMesh coalMesh;

    private final Map<WorldPickup, GameMesh> pickupMeshes = new HashMap<>();
    private final Map<WorldPickup, Scene> pickupScenes = new HashMap<>();

    @Override
    public void createView() {
        Vector3 startingPosition = new Vector3(0, 16f, 0);
        player = new Player(startingPosition);

        camera = new ViewCamera();

        PlayerMovementInputBoundary playerMovementInteractor = new PlayerMovementInteractor(player);

        //TESTING
        pickupStorage.addPickup(new WorldPickup(ItemTypes.COAL, new Vector3(5, 16, 0)));
        pickupStorage.addPickup(new WorldPickup(ItemTypes.WOOD_LOG, new Vector3(15, 16, 0)));
        pickupStorage.addPickup(new WorldPickup(ItemTypes.OIL_BARREL, new Vector3(25, 16, 0)));

        PickupInteractor pickupInteractor = new PickupInteractor(pickupStorage);
        PickupController pickupController = new PickupController(player, pickupInteractor);

        gameInputAdapter = new GameInputAdapter(playerMovementInteractor, player);
        Gdx.input.setInputProcessor(gameInputAdapter);
        Gdx.input.setCursorCatched(true);

        inventoryInputAdapter = new InventoryInputAdapter(player);
        pickupInputAdapter = new PickUpInputAdapter(pickupController);

        cameraController = new FirstPersonCameraController(camera, player);

        blockRepository = new InMemoryBlockRepository();
        materialRepository = new LibGDXMaterialRepository();

        // --- ENTITY SYSTEM INITIALIZATION ---
        colHandler = new CollisionHandler();

        GenerateZombieStrategy zombieGenerateStrategy = new GenerateZombieStrategy();
        GenerateZombieMeshStrategy zombieMeshStrategy = new GenerateZombieMeshStrategy();

        EntityStorage entityStorage = new IdToEntityStorage();
        EntityFactory entityFactory = new EntityFactory.EntityFactoryBuilder(entityStorage)
            .register(EntityType.ZOMBIE, zombieGenerateStrategy)
            .build();

        MeshStorage meshStorage = new IdToMeshStorage(colHandler);
        MeshFactory meshFactory = new MeshFactory.MeshFactoryBuilder(meshStorage)
            .register(EntityType.ZOMBIE, zombieMeshStrategy)
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
        hud = new GameHUD(player, pickupController);

        // TESTING === CREATE VISUAL + PHYSICS FOR EACH PICKUP ===
        for (WorldPickup pickup : pickupStorage.getAll()) {
            // 1) Physics / debug: red HitBox mesh
            HitBox hitBox = new HitBox("pickupHitbox", BOX, 2, 2, 2);
            GameMesh mesh = hitBox.Construct();
            mesh.transform.setToTranslation(pickup.getPosition());
            mesh.moving = false; // so ObjectRenderer doesn't drop it each frame
            objectRenderer.add(mesh);
            pickupMeshes.put(pickup, mesh);

            // 2) Visual model: load from assets
            Scene scene = ItemPickupSceneFactory.createSceneForPickup(pickup);
            objectRenderer.addToSceneManager(scene);
            pickupScenes.put(pickup, scene);
        }

        //test add entities
        Zombie zombie = new Zombie(objectRenderer);
        zombie.createZombie(); //delete this later
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
        block.dispose();
    }

    private void syncPickupVisuals() {
        Set<WorldPickup> current = new HashSet<>(pickupStorage.getAll());

        Iterator<Map.Entry<WorldPickup, GameMesh>> itMesh = pickupMeshes.entrySet().iterator();
        while (itMesh.hasNext()) {
            Map.Entry<WorldPickup, GameMesh> entry = itMesh.next();
            WorldPickup pickup = entry.getKey();
            GameMesh mesh = entry.getValue();

            if (!current.contains(pickup)) {
                objectRenderer.models.remove(mesh);
                // colHandler.remove(mesh); //  if we ever add collider removal
                itMesh.remove();
            }
        }

        Iterator<Map.Entry<WorldPickup, Scene>> itScene = pickupScenes.entrySet().iterator();
        while (itScene.hasNext()) {
            Map.Entry<WorldPickup, Scene> entry = itScene.next();
            WorldPickup pickup = entry.getKey();
            Scene scene = entry.getValue();
            if (!current.contains(pickup)) {
                objectRenderer.removeScene(scene);
                itScene.remove();
            }
        }
    }
}
