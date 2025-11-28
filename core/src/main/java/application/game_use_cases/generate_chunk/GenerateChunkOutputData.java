package application.game_use_cases.generate_chunk;

import domain.Chunk;

public class GenerateChunkOutputData {
    private final Chunk newChunk;
    GenerateChunkOutputData(Chunk chunk) {
        newChunk = chunk;
    }

    public Chunk getChunk() {
        return newChunk;
    }
}
