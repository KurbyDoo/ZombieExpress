package application.use_cases.unload_chunk;

import domain.Chunk;

public class UnloadChunkInputData {
    private final Chunk chunk;
    public UnloadChunkInputData(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
