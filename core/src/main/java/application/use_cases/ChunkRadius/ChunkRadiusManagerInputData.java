package application.use_cases.ChunkRadius;

import com.badlogic.gdx.math.Vector3;

import domain.entities.World;
import application.use_cases.chunk_generation.ChunkGenerationInputBoundary;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputBoundary;


/**
 * Input Data object for the ChunkRadiusManagerInteractor.
 * This bundles all dependencies and run-time parameters needed to execute the chunk radius logic.
 */
public class ChunkRadiusManagerInputData {
    public final Vector3 playerPosition;
    public final World world;
    public final ChunkGenerationInputBoundary generator;
    public final ChunkMeshGenerationInputBoundary meshGenerator;
    public final ChunkRadiusManagerOutputBoundary output;
    public final int radius;

    public ChunkRadiusManagerInputData(
        Vector3 playerPosition,
        World world,
        ChunkGenerationInputBoundary generator,
        ChunkMeshGenerationInputBoundary meshGenerator,
        ChunkRadiusManagerOutputBoundary output,
        int radius) {
        this.playerPosition = playerPosition;
        this.world = world;
        this.generator = generator;
        this.meshGenerator = meshGenerator;
        this.output = output;
        this.radius = radius;
    }
}
