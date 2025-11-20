package infrastructure.rendering;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import application.use_cases.ChunkRadius.ChunkRadiusManagerOutputBoundary;
import domain.entities.Chunk;
import domain.entities.World;
import physics.GameMesh;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements the ChunkRadiusManagerOutputBoundary.
 * This class handles the final rendering step, maintaining references to chunk models
 * and passing them to the central ObjectRenderer.
 * * Note: Since ChunkMeshData extends GameMesh, we pass the ChunkMeshData directly to the renderer.
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

        // Check if this chunk was previously rendered (e.g., if updated) and remove the old one first
        ChunkMeshData existingData = renderedChunks.get(chunkPos);
        if (existingData != null) {
            // Remove from the renderer and dispose its components (body, shape, triangle)
            objectRenderer.remove(existingData);
            renderedChunks.remove(chunkPos);
        }

        // Place the model instance at the correct world position
        float x = chunkPos.x * Chunk.CHUNK_SIZE;
        float y = chunkPos.y * Chunk.CHUNK_SIZE;
        float z = chunkPos.z * Chunk.CHUNK_SIZE;
        meshData.transform.trn(x, y, z); // Set ModelInstance transform
        meshData.body.setWorldTransform(meshData.transform); // Set Bullet transform

        // Add to the list and the renderer
        renderedChunks.put(new Vector3(chunkPos), meshData);
        objectRenderer.add(meshData);
    }

    // Phase 3 Action: Chunk removed from radius.
    @Override
    public void onChunkRemoved(Vector3 chunkPos) {
        ChunkMeshData removedData = renderedChunks.remove(chunkPos);
        if (removedData != null) {
            objectRenderer.remove(removedData); // Remove from render queue and collision handler
            removedData.modelDispose(); // Dispose the unique model for this chunk
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
