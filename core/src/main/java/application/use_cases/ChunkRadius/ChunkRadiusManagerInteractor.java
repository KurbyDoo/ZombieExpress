package application.use_cases.ChunkRadius;

import com.badlogic.gdx.math.Vector3;

import java.util.HashSet;
import java.util.Set;

import domain.entities.World;
import domain.entities.Chunk;

import application.use_cases.chunk_generation.ChunkGenerationInputBoundary;
import application.use_cases.chunk_generation.ChunkGenerationInputData;

// NEW IMPORTS for meshing pipeline
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputBoundary;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputData;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationOutputData;

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
        return true;
    }


    @Override
    public void execute(ChunkRadiusManagerInputData inputData) {

        // --- EXTRACT DATA FROM INPUT OBJECT ---
        final Vector3 playerPosition = inputData.playerPosition;
        final World world = inputData.world;
        final ChunkGenerationInputBoundary generator = inputData.generator;
        final ChunkMeshGenerationInputBoundary meshGenerator = inputData.meshGenerator;
        final ChunkRadiusManagerOutputBoundary output = inputData.output;

        final int X_VIEW_RANGE = inputData.radius;
        final int Z_CLAMP_DISTANCE = 10;
        final int Y_VIEW_RANGE = 3;

        int cx = (int) Math.floor(playerPosition.x / Chunk.CHUNK_SIZE);
        int cy = (int) Math.floor(playerPosition.y / Chunk.CHUNK_SIZE);
        int cz = (int) Math.floor(playerPosition.z / Chunk.CHUNK_SIZE);

        Set<Vector3> desired = new HashSet<>();
        Set<Chunk> newlyGeneratedChunks = new HashSet<>();

        // --- Z-AXIS CLAMPING LOGIC ---
        int startZ = Math.max(cz - Z_CLAMP_DISTANCE, MIN_WORLD_Z_CHUNKS);
        int endZ = Math.min(cz + Z_CLAMP_DISTANCE, MAX_WORLD_Z_CHUNKS);

        // --- PHASE 1: GENERATE ALL BLOCK DATA ---
        // This ensures all neighbors exist before any meshing happens
        for (int dx = -X_VIEW_RANGE; dx <= X_VIEW_RANGE; dx++) {
            for (int dy = -Y_VIEW_RANGE; dy <= Y_VIEW_RANGE; dy++) {
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

                        // 1. EXECUTE: Block generation only
                        generator.execute(new ChunkGenerationInputData(newChunk, world));

                        // 2. COLLECT: Add to the set for meshing in Phase 2
                        newlyGeneratedChunks.add(newChunk);
                        output.onChunkCreated(pos); // Signal existence
                    }
                }
            }
        }
        // --- PHASE 2: GENERATE MESHES FOR ALL NEW CHUNKS ---
        // This runs AFTER all neighboring block data is guaranteed to exist.
        for (Chunk chunk : newlyGeneratedChunks) {

            Vector3 chunkPos = chunk.getPosition();

            // 1. Generate the mesh
            ChunkMeshGenerationInputData meshInput = new ChunkMeshGenerationInputData(world, chunk);
            ChunkMeshGenerationOutputData meshOutput = meshGenerator.execute(meshInput);

            // 2. Submit the model instance to the OutputBoundary
            if (meshOutput.getMeshData() != null) {
                output.onChunkMeshReady(chunkPos, meshOutput.getMeshData());
            }
        }

        // --- PHASE 3: REMOVE CHUNKS OUTSIDE RADIUS ---
        Set<Vector3> toRemove = new HashSet<>();
        for (Vector3 pos : world.getChunkPositions()) {
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
