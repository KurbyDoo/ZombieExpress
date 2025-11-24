package application.use_cases.render_radius;

import com.badlogic.gdx.math.Vector3;
import infrastructure.rendering.ChunkMeshData;

public interface RenderRadiusManagerOutputBoundary {

    void onChunkCreated(Vector3 chunkPos);
    void onChunkRemoved(Vector3 chunkPos);
    void onChunkMeshReady(Vector3 chunkPos, ChunkMeshData meshData);
}
