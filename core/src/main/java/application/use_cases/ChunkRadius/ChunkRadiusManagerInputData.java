package application.use_cases.ChunkRadius;

import com.badlogic.gdx.math.Vector3;
import application.use_cases.chunk_generation.ChunkGenerationInputBoundary;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputBoundary;
import domain.entities.World;

public class ChunkRadiusManagerInputData {

    public final Vector3 playerPosition;
    public final World world;

    // Using your preferred variable names
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
