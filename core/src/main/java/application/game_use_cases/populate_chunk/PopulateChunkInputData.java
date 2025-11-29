package application.game_use_cases.populate_chunk;

import domain.Chunk;
import domain.World;

public class PopulateChunkInputData {
    private final Chunk chunk;

    public PopulateChunkInputData(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
