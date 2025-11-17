package application.use_cases.ChunkRadius;

import com.badlogic.gdx.math.Vector3;

import java.util.HashSet;
import java.util.Set;

import domain.entities.World;
import domain.entities.Chunk;

import application.use_cases.ChunkGeneration.*;

public class ChunkRadiusManagerInteractor implements ChunkRadiusManagerInputBoundary {
    private static final int MAX_WORLD_Z_CHUNKS = 8;
    private static final int MIN_WORLD_Z_CHUNKS = -8;

    private static final int MAX_WORLD_Y_CHUNKS = 32;
    private static final int MIN_WORLD_Y_CHUNKS = 0;

    private boolean isWithinWorldBounds(int x, int y, int z) {
        // 1. Check Z-Axis Hard Clamp
        if (z < MIN_WORLD_Z_CHUNKS || z > MAX_WORLD_Z_CHUNKS) {
            return false;
        }

        // 2. Check Y-Axis Build Height
        if (y < MIN_WORLD_Y_CHUNKS || y > MAX_WORLD_Y_CHUNKS) {
            return false;
        }

        // X is considered within bounds as the world is infinite along this axis.
        return true;
    }


    @Override
    public void execute(ChunkRadiusManagerInputData inputData) {

        // --- EXTRACT DATA FROM INPUT OBJECT ---
        final Vector3 playerPosition = inputData.playerPosition;
        final World world = inputData.world;
        final ChunkGenerationInputBoundary generator = inputData.generator;
        final ChunkRadiusManagerOutputBoundary output = inputData.output;
        // This radius controls the dynamic X-axis (Forward/Backward) view distance.
        final int X_VIEW_RANGE = inputData.radius;

        // Z-Axis (Left/Right): CLAMPED by MIN/MAX_WORLD_Z_CHUNKS.
        // This constant is a large number used to ensure the boundary clamps take priority.
        final int Z_CLAMP_DISTANCE = 10;

        final int Y_VIEW_RANGE = 3;  // Up/Down view: Fixed at 3 chunks both ways (7 total height)

        int cx = (int) Math.floor(playerPosition.x / Chunk.CHUNK_SIZE);
        int cy = (int) Math.floor(playerPosition.y / Chunk.CHUNK_SIZE);
        int cz = (int) Math.floor(playerPosition.z / Chunk.CHUNK_SIZE);

        Set<Vector3> desired = new HashSet<>();

        // --- Z-AXIS CLAMPING LOGIC ---
        int startZ = Math.max(cz - Z_CLAMP_DISTANCE, MIN_WORLD_Z_CHUNKS);
        int endZ = Math.min(cz + Z_CLAMP_DISTANCE, MAX_WORLD_Z_CHUNKS);

        // Generate all chunks inside the player's viewing box.

        // 1. X-Axis (forward/black)
        for (int dx = -X_VIEW_RANGE; dx <= X_VIEW_RANGE; dx++) {

            // 2. Y-Axis (Up/Down): FIXED view range of 3 chunks around player.
            for (int dy = -Y_VIEW_RANGE; dy <= Y_VIEW_RANGE; dy++) {

                // 3. Z-Axis (left/right): fixed world hard clamp
                for (int targetZ = startZ; targetZ <= endZ; targetZ++) {

                    int targetX = cx + dx;
                    int targetY = cy + dy;

                    if (!isWithinWorldBounds(targetX, targetY, targetZ)) {
                        continue;
                    }

                    Vector3 pos = new Vector3(targetX, targetY, targetZ);
                    desired.add(pos);

                    if (!world.hasChunk(targetX, targetY, targetZ)) {

                        Chunk newChunk = world.addChunk(targetX, targetY, targetZ);

                        generator.execute(new ChunkGenerationInputData(newChunk));
                        output.onChunkCreated(pos);
                    }
                }
            }
        }

        // Remove chunks outside radius
        Set<Vector3> toRemove = new HashSet<>();
        for (Vector3 pos : world.getChunkPositions()) {
            // We ensure chunks are removed if they leave the viewing box OR if they are outside the absolute world boundaries.
            if (!desired.contains(pos) || !isWithinWorldBounds((int)pos.x, (int)pos.y, (int)pos.z)) {
                toRemove.add(pos);
            }
        }

        for (Vector3 pos : toRemove) {
            world.removeChunk((int)pos.x, (int)pos.y, (int)pos.z);
            output.onChunkRemoved(pos);
        }
    }
}
