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

    private Stage uiStage;
    private Container<Label>[] hotbarSlots;
    private Label[] hotbarLabels;
    private Drawable slotNormalDrawable;
    private Drawable slotSelectedDrawable;
    private Label timeLabel;
    private float elapsedTime = 0;
    private Label distanceLabel;
    private float testDamageTimer = 0f; //TODO
    private Image healthBar;

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
        setupUI();

        //test add entities
        Zombie zombie = new Zombie(objectRenderer);
        zombie.createZombie(); //delete this later
    }

    @Override
    public void renderView() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        accumulator += deltaTime;
        elapsedTime += deltaTime;

        //TODO
        testDamageTimer += deltaTime;
        if (testDamageTimer >= 3f) {
            player.takeDamage(10);
            testDamageTimer = 0f;
        }

        while (accumulator >= TIME_STEP) {
            accumulator -= TIME_STEP;
            cameraController.updatePrevious();

            // --- GAME LOGIC ---
            gameInputAdapter.processInput(TIME_STEP);

            // Handle hotbar numeric key input
            handleHotbarKeyInput();

            gameSimulationController.update(TIME_STEP);
        }
        player.updatePassiveHealing(deltaTime);

        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
        objectRenderer.render(deltaTime);
        refreshTimeLabel();
        refreshDistanceLabel();
        refreshHealthBar();
        refreshHotbarSelection();
        uiStage.act(deltaTime);
        uiStage.draw();
    }


    @Override
    public void disposeView() {
        // Dispose world-related components first
        worldSyncController.dispose();

        objectRenderer.dispose();
        block.dispose();
    }

    @SuppressWarnings("unchecked")
    private void setupUI() {
        uiStage = new Stage(new ScreenViewport());
        BitmapFont uiFont = new BitmapFont();

        Label.LabelStyle timeStyle = new Label.LabelStyle(uiFont, Color.WHITE);
        timeLabel = new Label("", timeStyle);
        timeLabel.setFontScale(2);
        Table timeTable = new Table();
        timeTable.setFillParent(true);
        timeTable.top().left().padTop(10).padLeft(10);
        timeTable.add(timeLabel);
        uiStage.addActor(timeTable);
        refreshTimeLabel();

        Label.LabelStyle distanceStyle = new Label.LabelStyle(uiFont, Color.WHITE);
        distanceLabel = new Label("", distanceStyle);
        distanceLabel.setFontScale(2);
        Table distanceTable = new Table();
        distanceTable.setFillParent(true);
        distanceTable.top().padTop(10);
        distanceTable.add(distanceLabel).center();
        uiStage.addActor(distanceTable);
        refreshDistanceLabel();

        slotNormalDrawable = createSlotDrawable(Color.LIGHT_GRAY);
        slotSelectedDrawable = createSlotDrawable(Color.WHITE);
        Table hotbarTable = new Table();

        int HOTBAR_SIZE = 10;
        int SLOT_SIZE = 100;
        hotbarSlots = new Container[HOTBAR_SIZE];
        hotbarLabels = new Label[HOTBAR_SIZE];

        for (int i = 0; i < HOTBAR_SIZE; i++) {
            Label.LabelStyle style = new Label.LabelStyle(uiFont, Color.WHITE);
            Label label = new Label("", style);
            label.setAlignment(Align.center);

            Container<Label> slotContainer = new Container<>(label);
            slotContainer.width(SLOT_SIZE).height(SLOT_SIZE);
            slotContainer.background(slotNormalDrawable);

            hotbarTable.add(slotContainer);
            hotbarSlots[i] = slotContainer;
            hotbarLabels[i] = label;
        }

        hotbarTable.pack();
        float worldWidth  = uiStage.getViewport().getWorldWidth();
        float x = (worldWidth - hotbarTable.getWidth()) / 2f;
        float y = 0f;

        hotbarTable.setPosition(x, y);
        uiStage.addActor(hotbarTable);
        refreshHotbarSelection();

        Table healthTable = new Table();
        healthTable.setFillParent(true);
        healthTable.top().right().padTop(10).padRight(10);

        float healthBarMaxWidth = 260;
        float healthBarHeight = 30;
        Drawable redHealth = createBarDrawable(new Color(0.3f, 0, 0, 1), (int) healthBarMaxWidth, (int) healthBarHeight);
        Drawable greenHealth = createBarDrawable(Color.GREEN, (int) healthBarMaxWidth, (int) healthBarHeight);
        Drawable healthBorder = createBorderDrawable((int) healthBarMaxWidth, (int) healthBarHeight);

        Image healthBackground = new Image(redHealth);
        healthBar = new Image(greenHealth);
        Image healthBorderImage = new Image(healthBorder);

        Stack healthStack = new Stack();
        healthStack.add(healthBackground);
        healthStack.add(healthBar);
        healthStack.add(healthBorderImage);

        healthTable.add(healthStack).width(healthBarMaxWidth).height(healthBarHeight);
        uiStage.addActor(healthTable);
    }

    private Drawable createSlotDrawable(Color borderColor) {
        int size = 16;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();

        pixmap.setColor(borderColor);
        int thickness = 1;
        pixmap.fillRectangle(0, 0, size, thickness);
        pixmap.fillRectangle(0, size - thickness, size, thickness);
        pixmap.fillRectangle(0, 0, thickness, size);
        pixmap.fillRectangle(size - thickness, 0, thickness, size);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private Drawable createBarDrawable(Color color, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private Drawable createBorderDrawable(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0,0,0,0));
        pixmap.fill();
        pixmap.setColor(Color.BLACK);
        pixmap.fillRectangle(0, height - 3, width, 3);
        pixmap.fillRectangle(0, 0, width, 3);
        pixmap.fillRectangle(0, 0, 3, height);
        pixmap.fillRectangle(width - 3, 0, 3, height);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }


    private void refreshTimeLabel() {
        if (timeLabel == null) return;

        int totalSeconds = (int) elapsedTime;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String text = String.format("Time: %02d:%02d", minutes, seconds);
        timeLabel.setText(text);
    }

    private void refreshDistanceLabel() {
        Vector3 current = player.getPosition();
        Vector3 start = player.getStartingPosition();
        int distance = Math.max(0, (int)(current.x - start.x));
        String text = String.format("Distance: %d m", distance);
        distanceLabel.setText(text);
    }

    private void refreshHotbarSelection() {
        int selected = player.getCurrentSlot();

        for (int i = 0; i < hotbarSlots.length; i++) {
            if (i == selected) {
                hotbarSlots[i].background(slotSelectedDrawable);
            } else {
                hotbarSlots[i].background(slotNormalDrawable);
            }

            InventorySlot slot = player.getInventory().getSlot(i);

            if (slot == null || slot.isEmpty()) {
                hotbarLabels[i].setText("");
            } else {
                String baseName = slot.getItem().getName();
                String labelText;
                if (slot.getItem().isStackable()) {
                    labelText = baseName + " x" + slot.getQuantity();
                } else {
                    labelText = baseName;
                }
                hotbarLabels[i].setText(breakIntoLinesByWords(labelText));
            }
        }
    }

    private void refreshHealthBar() {
        int current = player.getCurrentHealth();
        int max = player.getMaxHealth();

        float ratio = (max > 0) ? (current / (float) max) : 0;
        if (ratio < 0) ratio = 0;
        if (ratio > 1) ratio = 1;
        healthBar.setScaleX(ratio);
    }

    private String breakIntoLinesByWords(String text) {
        String[] words = text.split(" ");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            sb.append(words[i]);
            if (i < words.length - 1)
                sb.append("\n");
        }
        return sb.toString();
    }

    private void handleHotbarKeyInput() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1)) {
            player.setCurrentSlot(0);
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2)) {
            player.setCurrentSlot(1);
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_3)) {
            player.setCurrentSlot(2);
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_4)) {
            player.setCurrentSlot(3);
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_5)) {
            player.setCurrentSlot(4);
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_6)) {
            player.setCurrentSlot(5);
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_7)) {
            player.setCurrentSlot(6);
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_8)) {
            player.setCurrentSlot(7);
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_9)) {
            player.setCurrentSlot(8);
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_0)) {
            player.setCurrentSlot(9);
        }
    }
}
