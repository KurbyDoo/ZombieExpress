/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Presentation/Controllers)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package presentation.controllers;

import application.game_use_cases.update_world.UpdateWorldInputBoundary;
import application.game_use_cases.update_world.UpdateWorldInputData;
import application.game_use_cases.update_world.UpdateWorldOutputData;
import com.badlogic.gdx.utils.Disposable;

import domain.Chunk;
import domain.GamePosition;
import infrastructure.rendering.*;

import java.util.List;
import java.util.Map;


/**
 * Manages all world-related systems, including data, generation, and rendering logic.
 * This acts as a service layer, delegating to use cases and infrastructure components.
 */
public class WorldSyncController implements Disposable {
    private final int RENDER_RADIUS;

    private final UpdateWorldInputBoundary worldUpdater;

    private final ChunkRenderer chunkRenderer;

    private Map<GamePosition, Chunk> toUnload;
    private List<Integer> activeEntities;

    /**
     * Creates and wires together all world components using provided dependencies.
     */
    public WorldSyncController(
        int renderRadius,
        UpdateWorldInputBoundary worldUpdater,
        ChunkRenderer chunkRenderer
    ) {
        this.RENDER_RADIUS = renderRadius;
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
        for (Map.Entry<GamePosition, Chunk> entry : outputData.getChunksToLoad().entrySet()) {
            chunkRenderer.loadChunk(entry.getKey(), entry.getValue());
        }

        activeEntities = outputData.getActiveEntities();
        toUnload = outputData.getChunksToUnload();
    }

    public List<Integer> getActiveEntities() {
        // Return the list of chunks currently loaded/updating
        return activeEntities;
    }

    public void unloadUpdate() {
        // Unload Chunks
        for (Map.Entry<GamePosition, Chunk> entry : toUnload.entrySet()) {
            chunkRenderer.unloadChunk(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void dispose() {
        // Ensure the renderer is disposed to clean up all chunk models/shapes
        chunkRenderer.dispose();
    }
}
