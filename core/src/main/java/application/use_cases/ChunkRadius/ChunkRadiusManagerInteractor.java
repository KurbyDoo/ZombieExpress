package application.use_cases.ChunkRadius;

import com.badlogic.gdx.math.Vector3;

import java.util.HashSet;
import java.util.Set;

import domain.entities.World;
import domain.entities.Chunk;

import application.use_cases.ChunkGeneration.*;

public class ChunkRadiusManagerInteractor implements ChunkRadiusManagerInputBoundary {
    private static final int MAX_X_CHUNKS = 8;
    private static final int MIN_X_CHUNKS = -8;
    private static final int MAX_Y_CHUNKS = 32;
    private static final int MIN_Y_CHUNKS = 0;

    private boolean isWithinWorldBounds(int x, int y, int z) {
        if (x < MIN_X_CHUNKS || x > MAX_X_CHUNKS) {
            return false;
        }

        if (y < MIN_Y_CHUNKS || y > MAX_Y_CHUNKS) {
            return false;
        }

        return true;
    }


    @Override
    public void execute(ChunkRadiusManagerInputData inputData) { // <-- MODIFIED SIGNATURE

        // --- EXTRACT DATA FROM INPUT OBJECT ---
        final Vector3 playerPosition = inputData.playerPosition;
        final World world = inputData.world;
        final ChunkGenerationInputBoundary generator = inputData.generator;
        final ChunkRadiusManagerOutputBoundary output = inputData.output;
        final int radius = inputData.radius;
        // --------------------------------------

        int cx = (int) Math.floor(playerPosition.x / Chunk.CHUNK_SIZE);
        int cy = (int) Math.floor(playerPosition.y / Chunk.CHUNK_SIZE);
        int cz = (int) Math.floor(playerPosition.z / Chunk.CHUNK_SIZE);

        Set<Vector3> desired = new HashSet<>();

        // Generate all chunks inside the player's viewing radius
        for (int dx = -radius; dx <= radius; dx++) {
            // NOTE: The vertical (Y) range is small for performance, only 3 chunks high.
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {

                    int targetX = cx + dx;
                    int targetY = cy + dy;
                    int targetZ = cz + dz;

                    // CHECK: ONLY LOAD CHUNKS WITHIN THE DEFINED WORLD BOUNDS
                    if (!isWithinWorldBounds(targetX, targetY, targetZ)) {
                        continue; // Skip this coordinate, it's outside the world bounds
                    }

                    Vector3 pos = new Vector3(targetX, targetY, targetZ);
                    desired.add(pos);

                    if (!world.hasChunk(targetX, targetY, targetZ)) {

                        Chunk newChunk = world.addChunk(targetX, targetY, targetZ);

                        // Use the injected generator dependency
                        generator.execute(new ChunkGenerationInputData(newChunk));
                        // Use the injected output dependency
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
            // Use the injected output dependency
            output.onChunkRemoved(pos);
        }
    }
}
