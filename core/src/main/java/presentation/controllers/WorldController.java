package presentation.controllers;

import application.use_cases.ChunkGeneration.ChunkGenerationInputBoundary;
import application.use_cases.ChunkGeneration.ChunkGenerationInteractor;
import application.use_cases.ChunkRadius.*;
import application.use_cases.ChunkRadius.ChunkRadiusManagerInputBoundary;
import application.use_cases.ChunkRadius.ChunkRadiusManagerInteractor;
import com.badlogic.gdx.math.Vector3;
import domain.entities.World;
import infrastructure.rendering.ChunkRenderer;
import infrastructure.rendering.GameMeshBuilder;
import infrastructure.rendering.ObjectRenderer;

/**
 * Manages all world-related systems, including data, generation, and rendering logic.
 * This class encapsulates the "tying together" of world components.
 */
public class WorldController {

    private final World world;
    private final GameMeshBuilder meshBuilder;
    private final ChunkGenerationInputBoundary chunkGenerator;
    private final ChunkRadiusManagerOutputBoundary chunkRenderer;
    private final ChunkRadiusManagerInputBoundary chunkRadiusManagerInteractor;
    private final int renderRadius;

    /**
     * Creates and wires together all world components.
     * @param objectRenderer The main renderer, needed by the ChunkRenderer.
     * @param renderRadius The chunk radius to load around the player.
     */
    public WorldController(ObjectRenderer objectRenderer, int renderRadius) {
        this.renderRadius = renderRadius;
        this.world = new World();
        this.meshBuilder = new GameMeshBuilder(this.world);

        this.chunkGenerator = new ChunkGenerationInteractor();
        this.chunkRenderer = new ChunkRenderer(this.meshBuilder, objectRenderer, this.world);

        this.chunkRadiusManagerInteractor = new ChunkRadiusManagerInteractor();
    }

    /**
     * Updates the world state based on the player's current position.
     * This constructs the InputData and triggers the ChunkRadiusManager.
     * @param playerPosition The player's current position.
     */
    public void update(Vector3 playerPosition) {
        // 1. Assemble the Input Data package, feeding all dependencies to the Interactor
        ChunkRadiusManagerInputData inputData = new ChunkRadiusManagerInputData(
            playerPosition,
            this.world,
            this.chunkGenerator,
            this.chunkRenderer,
            this.renderRadius
        );

        this.chunkRadiusManagerInteractor.execute(inputData);
    }

    /**
     * Gets the world data object.
     * @return The World entity.
     */
    public World getWorld() {
        return this.world;
    }
}
