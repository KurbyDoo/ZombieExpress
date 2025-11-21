package infrastructure.rendering;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import application.use_cases.ChunkRadius.ChunkRadiusManagerOutputBoundary;
import domain.entities.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements the ChunkRadiusManagerOutputBoundary.
 * This class handles the final rendering step, maintaining references to chunk models
 * and passing them to the central ObjectRenderer.
 *
 * It is crucial for disposal: when a chunk leaves the render radius, this class
 * ensures its 3D model and physics body are correctly cleaned up.
 */

public class ChunkRenderer implements ChunkRadiusManagerOutputBoundary, Disposable {

    private final ObjectRenderer objectRenderer;
    private final World world;

    private final Map<Vector3, ChunkMeshData> renderedChunks;

    public ChunkRenderer(ObjectRenderer objectRenderer, World world) {
        this.objectRenderer = objectRenderer;
        this.world = world;
        this.renderedChunks = new ConcurrentHashMap<>();
    }

    @Override
    public void onChunkCreated(Vector3 chunkPos) {
        // No action needed here yet, waiting for Phase 2: MeshReady
    }

    // Phase 2 Action: Mesh data is ready, render it.
    @Override
    public void onChunkMeshReady(Vector3 chunkPos, ChunkMeshData meshData) {
        if (meshData == null) {
            return;
        }

        // --- 1. Clean up old mesh if re-meshing/updating the chunk ---
        ChunkMeshData existingData = renderedChunks.get(chunkPos);
        if (existingData != null) {
            // Remove from the renderer's queue and collision handler
            objectRenderer.remove(existingData);

            existingData.modelDispose();

            renderedChunks.remove(chunkPos);
        }

        // --- 2. Add the new mesh ---
        renderedChunks.put(new Vector3(chunkPos), meshData);
        objectRenderer.add(meshData); // Adds to render queue and collision handler
    }

    // Phase 3 Action: Chunk removed from radius.
    @Override
    public void onChunkRemoved(Vector3 chunkPos) {
        ChunkMeshData removedData = renderedChunks.remove(chunkPos);
        if (removedData != null) {
            objectRenderer.remove(removedData); // Remove from render queue and collision handler
            // Dispose the unique model resource
            removedData.modelDispose();
        }
    }

    @Override
    public void dispose() {
        // Clean up all rendered chunk data when the application exits
        for (ChunkMeshData data : renderedChunks.values()) {
            data.dispose(); // Dispose body/shape/triangle
            data.modelDispose(); // Dispose the unique model
        }
        renderedChunks.clear();
    }
}
