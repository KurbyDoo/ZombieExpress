package application.use_cases.chunk_mesh_generation;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class ChunkMeshGenerationOutputData {
    private ModelInstance model;

    public ChunkMeshGenerationOutputData(ModelInstance model) {
        this.model = model;
    }

    public ModelInstance getModel() { return model; }
}
