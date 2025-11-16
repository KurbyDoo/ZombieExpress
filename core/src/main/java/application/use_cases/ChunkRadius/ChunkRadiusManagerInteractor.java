package application.use_cases.ChunkRadius;

import com.badlogic.gdx.math.Vector3;

import java.util.HashSet;
import java.util.Set;

import domain.entities.World;
import domain.entities.Chunk;

import application.use_cases.ChunkGeneration.*;

public class ChunkRadiusManagerInteractor implements ChunkRadiusManagerInputBoundary {

    private final World world;
    private final ChunkGenerationInputBoundary generator;
    private final ChunkRadiusManagerOutputBoundary output;
    private final int radius;

    public ChunkRadiusManagerInteractor(
        World world,
        ChunkGenerationInputBoundary generator,
        ChunkRadiusManagerOutputBoundary output,
        int radius) {

        this.world = world;
        this.generator = generator;
        this.output = output;
        this.radius = radius;
    }

    @Override
    public void execute(Vector3 playerPosition) {

        int cx = (int) Math.floor(playerPosition.x / Chunk.CHUNK_SIZE);
        int cy = (int) Math.floor(playerPosition.y / Chunk.CHUNK_SIZE);
        int cz = (int) Math.floor(playerPosition.z / Chunk.CHUNK_SIZE);

        Set<Vector3> desired = new HashSet<>();

        // Generate all chunks inside radius
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {

                    Vector3 pos = new Vector3(cx + dx, cy + dy, cz + dz);
                    desired.add(pos);

                    if (!world.hasChunk((int)pos.x, (int)pos.y, (int)pos.z)) {

                        Chunk newChunk = world.addChunk(
                            (int) pos.x,
                            (int) pos.y,
                            (int) pos.z);

                        generator.execute(new ChunkGenerationInputData(newChunk));
                        output.onChunkCreated(pos);
                    }
                }
            }
        }

        // Remove chunks outside radius
        Set<Vector3> toRemove = new HashSet<>();
        for (Vector3 pos : world.getChunkPositions()) {
            if (!desired.contains(pos)) {
                toRemove.add(pos);
            }
        }

        for (Vector3 pos : toRemove) {
            world.removeChunk((int)pos.x, (int)pos.y, (int)pos.z);
            output.onChunkRemoved(pos);
        }
    }
}
