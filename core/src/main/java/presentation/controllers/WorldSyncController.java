package presentation.controllers;

import application.use_cases.populate_chunk.PopulateChunkInputData;
import application.use_cases.update_world.UpdateWorldInputBoundary;
import application.use_cases.update_world.UpdateWorldInputData;
import application.use_cases.update_world.UpdateWorldOutputData;
import com.badlogic.gdx.utils.Disposable;

import domain.Chunk;
import domain.GamePosition;
import domain.World;
import infrastructure.rendering.*;


import java.util.Set;


/**
 * Manages all world-related systems, including data, generation, and rendering logic.
 * This acts as a service layer, delegating to use cases and infrastructure components.
 */
public class WorldSyncController implements Disposable {
    private final World world;
    private final int RENDER_RADIUS;

    private final UpdateWorldInputBoundary worldUpdater;

    private final ChunkRenderer chunkRenderer;

    private Set<GamePosition> toUnload;
    private Set<GamePosition> toUpdate;

    /**
     * Creates and wires together all world components using provided dependencies.
     */
    public WorldSyncController(
        int renderRadius,
        World world,
        UpdateWorldInputBoundary worldUpdater,
        ChunkRenderer chunkRenderer
    ) {
        this.RENDER_RADIUS = renderRadius;
        this.world = world;
        this.worldUpdater = worldUpdater;
        this.chunkRenderer = chunkRenderer;}

    /**
     * Updates the world state based on the player's current position, triggering
     * chunk generation, meshing, and de-rendering.
     */
    public void loadUpdate() {

        UpdateWorldOutputData outputData = worldUpdater.execute(
            new UpdateWorldInputData(RENDER_RADIUS)
        );

        // Load Chunks
        for (GamePosition pos : outputData.getChunksToLoad()) {
            Chunk chunk = world.getChunk(pos);
            chunkRenderer.loadChunk(pos, chunk);
        }

        toUpdate = outputData.getChunksToUpdate();
        toUnload = outputData.getChunksToUnload();
    }

    public Set<GamePosition> getActiveChunkPositions() {
        // Return the list of chunks currently loaded/updating
        return toUpdate;
    }

    public void unloadUpdate() {
        // Unload Chunks
        for (GamePosition pos : toUnload) {
            Chunk chunk = world.getChunk(pos);
            chunkRenderer.unloadChunk(pos, chunk);
        }
    }

    @Override
    public void dispose() {
        // Ensure the renderer is disposed to clean up all chunk models/shapes
        chunkRenderer.dispose();
    }
}
