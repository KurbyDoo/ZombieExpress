package application.use_cases.render_radius;

import domain.GamePosition;
import infrastructure.rendering.ChunkMeshData;

public interface RenderRadiusManagerOutputBoundary {

    void onChunkCreated(GamePosition chunkPos);
    void onChunkRemoved(GamePosition chunkPos);
    void onChunkMeshReady(GamePosition chunkPos, ChunkMeshData meshData);
}
