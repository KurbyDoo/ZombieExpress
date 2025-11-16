package application.use_cases.ChunkGeneration;

import domain.entities.Chunk;
import domain.entities.World;

public class ChunkGenerationInputData {
    private final Chunk chunk;
    private final World world;
    public ChunkGenerationInputData(Chunk chunk, World world) {
        this.chunk = chunk;
        this.world = world;
    }

    public Chunk getChunk() { return chunk; }
    public World getWorld() { return world; }
}
