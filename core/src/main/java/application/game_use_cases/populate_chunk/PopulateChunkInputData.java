package application.game_use_cases.populate_chunk;

import domain.Chunk;
import domain.World;

public class PopulateChunkInputData {
    private final Chunk chunk;
    private final World world;

    public PopulateChunkInputData(World world, Chunk chunk) {
        this.world = world;
        this.chunk = chunk;
    }

    public World getWorld() {
        return world;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
