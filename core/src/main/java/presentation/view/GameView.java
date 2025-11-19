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
import application.use_cases.ports.BlockRepository;
import data_access.InMemoryBlockRepository;
import domain.entities.Player;
import domain.entities.World;
import domain.entities.ZombieStorage;
import infrastructure.input_boundary.GameInputAdapter;
import infrastructure.input_boundary.UIInputAdapter;
import infrastructure.rendering.*;
import physics.CollisionHandler;
import physics.GameMesh;
import physics.HitBox;
import presentation.ZombieInstanceUpdater;
import presentation.controllers.CameraController;
import presentation.controllers.EntityController;
import presentation.controllers.FirstPersonCameraController;
import presentation.controllers.WorldGenerationController;
import static physics.HitBox.ShapeTypes.SPHERE;

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

    private GameHUD hud;
    private UIInputAdapter uiInputAdapter;

    @Override
    public void createView() {
        Vector3 startingPosition = new Vector3(0, 16f, 0);
        player = new Player(startingPosition);

        camera = new ViewCamera();

        PlayerMovementInputBoundary playerMovementInteractor = new PlayerMovementInteractor(player);

        gameInputAdapter = new GameInputAdapter(playerMovementInteractor);
        Gdx.input.setInputProcessor(gameInputAdapter);
        Gdx.input.setCursorCatched(true);

        uiInputAdapter = new UIInputAdapter(player);

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
        hud = new GameHUD(player);

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

            // UI inventory input (hotbar keys) – updates Player only
            uiInputAdapter.pollInput();

            gameSimulationController.update(TIME_STEP);
        }
        player.updatePassiveHealing(deltaTime);

        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
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
}
