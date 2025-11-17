package infrastructure.rendering;

import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputBoundary;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputData;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInteractor;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationOutputData;
import application.use_cases.ports.BlockRepository;
import domain.entities.Chunk;
import domain.entities.World;

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
