package application.use_cases.ChunkRadius;

import com.badlogic.gdx.math.Vector3;
import infrastructure.rendering.ChunkMeshData;

public interface ChunkRadiusManagerOutputBoundary {

    void onChunkCreated(Vector3 chunkPos);
    void onChunkRemoved(Vector3 chunkPos);
    void onChunkMeshReady(Vector3 chunkPos, ChunkMeshData meshData);
}
