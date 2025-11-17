package application.use_cases.chunk_mesh_generation;

import domain.entities.Chunk;
import domain.entities.World;

public class ChunkMeshGenerationInputData {
    private final World world;
    private final Chunk chunk;

    public ChunkMeshGenerationInputData(World world, Chunk chunk) {
        this.world = world;
        this.chunk = chunk;
    }

    public World getWorld() { return world; }
    public Chunk getChunk() { return chunk; }
}
