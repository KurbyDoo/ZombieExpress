package application.use_cases.chunk_mesh_generation;

import infrastructure.rendering.ChunkMeshData;

public class ChunkMeshGenerationOutputData {
    private ChunkMeshData meshData;

    public ChunkMeshGenerationOutputData(ChunkMeshData meshData) {
        this.meshData = meshData;
    }

    public ChunkMeshData getMeshData() { return meshData; }
}
