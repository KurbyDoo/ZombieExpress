package presentation.view;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.physics.bullet.collision.*;
import application.use_cases.EntityGeneration.EntityGenerationInteractor;
import application.use_cases.RenderZombie.RenderZombieInputData;
import application.use_cases.RenderZombie.RenderZombieInteractor;
import domain.entities.Player;
import domain.entities.World;
import physics.GameObject;
import physics.HitBox;
import domain.entities.ZombieStorage;
import presentation.ZombieInstanceUpdater;
import presentation.controllers.CameraController;
import presentation.controllers.EntityController;
import presentation.controllers.FirstPersonCameraController;
import infrastructure.input_boundary.GameInputAdapter;
import application.use_cases.ChunkGeneration.ChunkGenerationInteractor;
import application.use_cases.PlayerMovement.PlayerMovementInputBoundary;
import application.use_cases.PlayerMovement.PlayerMovementInteractor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import domain.entities.InventorySlot;

public class GameView implements Viewable{
    private final float FPS = 120.0f;
    private final float TIME_STEP = 1.0f / FPS;

    public ObjectRenderer objectRenderer;
    public GameMeshBuilder meshBuilder;
    public World world;
    private CameraController cameraController;
    private GameInputAdapter gameInputAdapter;
    private ViewCamera camera;
    private ChunkLoader chunkLoader;
    private WorldGenerationController worldGenerationController;
    private ChunkGenerationInteractor chunkGenerationUseCase;
    private Player player;

    private float accumulator;

    private Stage uiStage;
    private Container<Label>[] hotbarSlots;
    private Label[] hotbarLabels;
    private Drawable slotNormalDrawable;
    private Drawable slotSelectedDrawable;
    private Label timeLabel;
    private float elapsedTime = 0;

    private GameObject block;

    // add EntityController
    private EntityController entityController;
    private EntityGenerationInteractor entityGenerationInteractor;
    private RenderZombieInteractor renderZombieInteractor;

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

        objectRenderer = new ObjectRenderer(camera);
        world = new World();
        meshBuilder = new GameMeshBuilder(world);
        chunkLoader = new ChunkLoader(meshBuilder, objectRenderer);
        chunkGenerationUseCase = new ChunkGenerationInteractor();

        worldGenerationController = new WorldGenerationController(chunkGenerationUseCase, world, chunkLoader);

        worldGenerationController.generateInitialWorld(8, 4, 32);

        setupUI();

//        block = (new HitBox("Red", BOX, 30, 600, 30)).Construct();
//        objectRenderer.add(block);

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
    }

    @Override
    public void renderView() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        accumulator += deltaTime;
        elapsedTime += deltaTime;

        while (accumulator >= TIME_STEP) {
            accumulator -= TIME_STEP;
            cameraController.updatePrevious();

            // WORLD UPDATES
            gameInputAdapter.processInput(deltaTime);

            // Handle hotbar numeric key input
            handleHotbarKeyInput();

            // Call entity controller and pass world and entity list

        }


        // BACKGROUND PROCESSING
        chunkLoader.loadChunks();

        float alpha = accumulator / TIME_STEP;

        // RENDER UPDATES
        cameraController.renderCamera(alpha);
        entityController.renderZombie();
        objectRenderer.render(deltaTime);
        refreshTimeLabel();
        refreshHotbarSelection();
        uiStage.act(deltaTime);
        uiStage.draw();
    }

    @Override
    public void disposeView() {
        objectRenderer.dispose();

//        chunkMeshManager.dispose();

//        collisionWorld.dispose();
//        broadPhase.dispose();
//        dispatcher.dispose();
//        collisionConfig.dispose();


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
        //float worldHeight = uiStage.getViewport().getWorldHeight();
        float x = (worldWidth - hotbarTable.getWidth()) / 2f;
        float y = 0f;

        hotbarTable.setPosition(x, y);
        uiStage.addActor(hotbarTable);
        refreshHotbarSelection();
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

    private void refreshTimeLabel() {
        if (timeLabel == null) return;

        int totalSeconds = (int) elapsedTime;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String text = String.format("Time: %02d:%02d", minutes, seconds);
        timeLabel.setText(text);
    }

    private void refreshHotbarSelection() {
        if (hotbarSlots == null) return;

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
