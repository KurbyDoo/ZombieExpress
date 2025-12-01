package application.game_features.populate_chunk;

import domain.Chunk;

public class PopulateChunkInputData {
    private final Chunk chunk;

    public PopulateChunkInputData(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
