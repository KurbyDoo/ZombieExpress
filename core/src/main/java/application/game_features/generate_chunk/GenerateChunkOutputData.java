package application.game_features.generate_chunk;

import domain.world.Chunk;

public class GenerateChunkOutputData {
    private final Chunk newChunk;

    GenerateChunkOutputData(Chunk chunk) {
        newChunk = chunk;
    }

    public Chunk getChunk() {
        return newChunk;
    }
}
