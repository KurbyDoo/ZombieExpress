package application.use_cases.ChunkRadius;

import com.badlogic.gdx.math.Vector3;

public interface ChunkRadiusManagerOutputBoundary {

    void onChunkCreated(Vector3 chunkPos);
    void onChunkRemoved(Vector3 chunkPos);
}
