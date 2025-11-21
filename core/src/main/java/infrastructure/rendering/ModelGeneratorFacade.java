package infrastructure.rendering;

import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputBoundary;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputData;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInteractor;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationOutputData;
import application.use_cases.ports.BlockRepository;
import domain.entities.Chunk;
import domain.entities.World;

/**
 * RESEARCH NOTE: MODEL GENERATOR FACADE - INTEGRATION POINT
 * ==========================================================
 * 
 * This facade coordinates chunk mesh generation by delegating to the use case layer.
 * It's a key integration point between infrastructure and application layers.
 * 
 * CURRENT ROLE:
 * - Creates ChunkMeshGenerationInteractor with dependencies
 * - Translates domain objects (Chunk, World) into use case input
 * - Returns infrastructure objects (ChunkMeshData)
 * 
 * CLEAN ARCHITECTURE PATTERN:
 * This follows the Dependency Inversion Principle:
 * - Facade (infrastructure) depends on use case interfaces
 * - Use cases depend on ports (interfaces), not concrete implementations
 * - Domain layer has no dependencies
 * 
 * TEXTURE INTEGRATION CHANGES:
 * When adding texture support, this facade will need:
 * 
 * 1. New Dependency: TextureAtlasRepository
 *    ```java
 *    public ModelGeneratorFacade(
 *        World world, 
 *        BlockRepository blockRepository, 
 *        BlockMaterialRepository materialRepository,
 *        TextureAtlasRepository textureRepository  // NEW
 *    ) {
 *        this.world = world;
 *        this.chunkMeshBuilder = new ChunkMeshGenerationInteractor(
 *            blockRepository, 
 *            materialRepository,
 *            textureRepository  // Pass to interactor
 *        );
 *    }
 *    ```
 * 
 * 2. Potential Return Type Change (for Scene integration):
 *    ```java
 *    // Option A: Keep ChunkMeshData, add Scene wrapper internally
 *    public ChunkMeshData buildModel(Chunk chunk) {
 *        // Existing implementation works
 *    }
 *    
 *    // Option B: Return Scene-based object directly
 *    public ChunkSceneData buildScene(Chunk chunk) {
 *        ChunkMeshGenerationOutputData outputData = chunkMeshBuilder.execute(inputData);
 *        ChunkMeshData meshData = outputData.getMeshData();
 *        return new ChunkSceneData(meshData);  // Wraps in Scene
 *    }
 *    ```
 * 
 * RECOMMENDED APPROACH:
 * Keep this facade simple and focused. Major changes should happen in:
 * - ChunkMeshGenerationInteractor (mesh + UV generation)
 * - LibGDXMaterialRepository (texture materials)
 * - ChunkMeshData (Scene wrapper)
 * 
 * This facade only needs to wire dependencies correctly.
 * 
 * FUTURE ENHANCEMENT - Async Generation:
 * For the TODO about multithreading in ChunkLoader, this facade could:
 * ```java
 * public CompletableFuture<ChunkMeshData> buildModelAsync(Chunk chunk) {
 *     return CompletableFuture.supplyAsync(() -> {
 *         // Heavy mesh generation on worker thread
 *         ChunkMeshGenerationInputData inputData = 
 *             new ChunkMeshGenerationInputData(world, chunk);
 *         ChunkMeshGenerationOutputData outputData = 
 *             chunkMeshBuilder.execute(inputData);
 *         return outputData.getMeshData();
 *     });
 *     // Scene creation would still happen on render thread
 * }
 * ```
 */
public class ModelGeneratorFacade {
    private final World world;
    private final ChunkMeshGenerationInputBoundary chunkMeshBuilder;
    public ModelGeneratorFacade(World world, BlockRepository blockRepository, BlockMaterialRepository materialRepository) {
        this.world = world;
        chunkMeshBuilder = new ChunkMeshGenerationInteractor(blockRepository, materialRepository);
    }

    public ChunkMeshData buildModel(Chunk chunk) {
        ChunkMeshGenerationInputData inputData = new ChunkMeshGenerationInputData(world, chunk);
        ChunkMeshGenerationOutputData outputData = chunkMeshBuilder.execute(inputData);
        return outputData.getMeshData();
    }
}
