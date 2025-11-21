package infrastructure.rendering;

import domain.entities.Chunk;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * RESEARCH NOTE: CHUNK LOADER - SCENE INTEGRATION CONSIDERATIONS
 * ===============================================================
 * 
 * CURRENT IMPLEMENTATION:
 * - Loads chunks from queue
 * - Generates ChunkMeshData via ModelGeneratorFacade
 * - Adds to ObjectRenderer (ModelBatch rendering)
 * 
 * SCENE-BASED RENDERING INTEGRATION:
 * 
 * APPROACH 1: Minimal Change - Wrapper Method
 * --------------------------------------------
 * Keep current flow, add Scene conversion step:
 * 
 * ```java
 * public void loadChunks() {
 *     Chunk chunk;
 *     for (int i = 0; i < BUFFER_SIZE && ((chunk = chunksToLoad.poll()) != null); i++) {
 *         ChunkMeshData chunkMesh = meshBuilder.buildModel(chunk);
 *         if (chunkMesh == null) continue;
 *         
 *         // NEW: Convert to Scene and add to SceneManager
 *         Scene chunkScene = chunkMesh.getScene();
 *         objectRenderer.addToSceneManager(chunkScene);
 *         
 *         // Still add for physics
 *         objectRenderer.add(chunkMesh);
 *     }
 * }
 * ```
 * 
 * APPROACH 2: Separate Render/Physics Paths
 * ------------------------------------------
 * Decouple rendering from physics:
 * 
 * ```java
 * public void loadChunks() {
 *     Chunk chunk;
 *     for (int i = 0; i < BUFFER_SIZE && ((chunk = chunksToLoad.poll()) != null); i++) {
 *         ChunkMeshData chunkMesh = meshBuilder.buildModel(chunk);
 *         if (chunkMesh == null) continue;
 *         
 *         // Add to rendering system (SceneManager)
 *         objectRenderer.addChunkScene(chunkMesh.getScene());
 *         
 *         // Add to physics system (CollisionHandler)
 *         objectRenderer.addChunkPhysics(chunkMesh.getPhysics());
 *     }
 * }
 * ```
 * 
 * CLEAN ARCHITECTURE CONSIDERATIONS:
 * ===================================
 * - ChunkLoader is in infrastructure layer (correct)
 * - Depends on domain entities (Chunk) and infrastructure (ObjectRenderer)
 * - Should NOT know about rendering implementation details
 * - Interface between loader and renderer could be abstracted
 * 
 * Improved with Dependency Inversion:
 * ```java
 * public interface ChunkRenderer {
 *     void addChunk(ChunkMeshData chunkMesh);
 * }
 * 
 * public class ChunkLoader {
 *     private final ChunkRenderer renderer;  // Not ObjectRenderer directly
 *     // ...
 * }
 * ```
 * 
 * MULTITHREADING CONSIDERATIONS:
 * ===============================
 * The TODO mentions eventual multithreading. Key points:
 * - Mesh generation (CPU intensive) can be on worker thread
 * - Scene creation (GPU resource) must be on render thread
 * - Queue management already supports async with BlockingQueue
 * 
 * Example:
 * ```java
 * // Worker thread
 * MeshGenerationResult result = meshBuilder.buildModelData(chunk);
 * 
 * // Render thread (via Gdx.app.postRunnable)
 * Scene scene = createSceneFromMeshData(result);
 * sceneManager.addScene(scene);
 * ```
 */
// TODO: Eventually make this multithreaded
public class ChunkLoader {
    private BlockingQueue<Chunk> chunksToLoad;
    private final ModelGeneratorFacade meshBuilder;
    private final ObjectRenderer objectRenderer;

    private final int BUFFER_SIZE = 32;

    public ChunkLoader(ModelGeneratorFacade meshBuilder, ObjectRenderer objectRenderer) {
        this.meshBuilder = meshBuilder;
        this.objectRenderer = objectRenderer;
        chunksToLoad = new LinkedBlockingQueue<>();
    }

    public void addChunkToLoad(Chunk chunk) {
        chunksToLoad.add(chunk);
    }

    public void loadChunks() {
        try {
            Chunk chunk;
            for (int i = 0; i < BUFFER_SIZE && ((chunk = chunksToLoad.poll()) != null); i++) {
                ChunkMeshData chunkMesh = meshBuilder.buildModel(chunk);
                if (chunkMesh == null) continue;
                objectRenderer.add(chunkMesh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
