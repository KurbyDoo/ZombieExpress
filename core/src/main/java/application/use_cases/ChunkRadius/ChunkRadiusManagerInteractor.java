package application.use_cases.ChunkRadius;

import application.use_cases.chunk_generation.ChunkGenerationInputData;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputData;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationOutputData;
import domain.entities.Chunk;
import domain.entities.World;
import com.badlogic.gdx.math.Vector3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



public class ChunkRadiusManagerInteractor implements ChunkRadiusManagerInputBoundary {
    // --- WORLD BOUNDARY CONSTANTS ---
    private static final int MAX_WORLD_Z_CHUNKS = 8;
    private static final int MIN_WORLD_Z_CHUNKS = -8;

    private static final int MAX_WORLD_X_CHUNKS = 32;
    private static final int MIN_WORLD_X_CHUNKS = -8;

    // Y-Axis: Height limits
    private static final int MAX_WORLD_Y_CHUNKS = 32;
    private static final int MIN_WORLD_Y_CHUNKS = 0;

    private static final int Y_VIEW_RANGE = 3;

    // State to track player movement
    private int lastChunkX = Integer.MAX_VALUE;
    private int lastChunkZ = Integer.MAX_VALUE;
    private boolean firstRun = true;

    // Tracks chunks currently in the world
    private final Set<Vector3> generatedChunks = new HashSet<>();
    private final Set<Vector3> renderedChunks = new HashSet<>();

    private boolean isWithinWorldBounds(int x, int y, int z) {
        if (z < MIN_WORLD_Z_CHUNKS || z > MAX_WORLD_Z_CHUNKS) {
            return false;
        }

        if (x < MIN_WORLD_X_CHUNKS || x > MAX_WORLD_X_CHUNKS) {
            return false;
        }

        if (y < MIN_WORLD_Y_CHUNKS || y > MAX_WORLD_Y_CHUNKS) {
            return false;
        }
        return true;
    }

    @Override
    public void execute(ChunkRadiusManagerInputData inputData) {
        World world = inputData.world;
        Vector3 playerPos = inputData.playerPosition;

        // Calculate which chunk the player is currently in
        int currentChunkX = (int) Math.floor(playerPos.x / Chunk.CHUNK_SIZE);
        int currentChunkZ = (int) Math.floor(playerPos.z / Chunk.CHUNK_SIZE);

        // LAG FIX: Only update if the player has moved to a DIFFERENT chunk (or it's the start).
        if (!firstRun && currentChunkX == lastChunkX && currentChunkZ == lastChunkZ) {
            return;
        }

        // Update state for next time
        lastChunkX = currentChunkX;
        lastChunkZ = currentChunkZ;
        firstRun = false;

        // Use your variable names
        final int RENDER_RADIUS = inputData.radius;
        // Generation is 1 step larger than Mesh to avoid black spaces
        final int GENERATION_RADIUS = RENDER_RADIUS + 1;

        Set<Vector3> targetGeneratedChunks = new HashSet<>();
        Set<Vector3> targetRenderedChunks = new HashSet<>();

        // --- CALCULATE NEW AREA ---

        int zStart = Math.max(currentChunkZ - GENERATION_RADIUS, MIN_WORLD_Z_CHUNKS);
        int zEnd = Math.min(currentChunkZ + GENERATION_RADIUS, MAX_WORLD_Z_CHUNKS);

        int xStart = Math.max(currentChunkX - GENERATION_RADIUS, MIN_WORLD_X_CHUNKS);
        int xEnd = Math.min(currentChunkX + GENERATION_RADIUS, MAX_WORLD_X_CHUNKS);
        
        for (int targetX = xStart; targetX <= xEnd; targetX++) {
            for (int targetZ = zStart; targetZ <= zEnd; targetZ++) {
                for (int y = -Y_VIEW_RANGE; y <= Y_VIEW_RANGE; y++) {

                    if (!isWithinWorldBounds(targetX, y, targetZ)) continue;

                    Vector3 chunkPos = new Vector3(targetX, y, targetZ);
                    targetGeneratedChunks.add(chunkPos);

                    // Only mesh if within the smaller render radius
                    if (isWithinRadius(targetX, targetZ, currentChunkX, currentChunkZ, RENDER_RADIUS)) {
                        targetRenderedChunks.add(chunkPos);
                    }
                }
            }
        }

        // --- CLEANUP (Delete from where we came from) ---

        Iterator<Vector3> renderedIterator = renderedChunks.iterator();
        while (renderedIterator.hasNext()) {
            Vector3 pos = renderedIterator.next();
            if (!targetRenderedChunks.contains(pos)) {
                inputData.output.onChunkRemoved(pos);
                renderedIterator.remove();
            }
        }

        Iterator<Vector3> generatedIterator = generatedChunks.iterator();
        while (generatedIterator.hasNext()) {
            Vector3 pos = generatedIterator.next();
            if (!targetGeneratedChunks.contains(pos)) {
                world.removeChunk((int)pos.x, (int)pos.y, (int)pos.z);
                generatedIterator.remove();
            }
        }

        // --- GENERATE NEW CHUNKS ---
        for (Vector3 pos : targetGeneratedChunks) {
            if (!generatedChunks.contains(pos)) {
                if (!world.hasChunk((int)pos.x, (int)pos.y, (int)pos.z)) {
                    Chunk newChunk = world.addChunk((int)pos.x, (int)pos.y, (int)pos.z);
                    inputData.generator.execute(new ChunkGenerationInputData(newChunk, world));
                    generatedChunks.add(pos);
                    inputData.output.onChunkCreated(pos);
                } else {
                    generatedChunks.add(pos);
                }
            }
        }

        // --- MESH NEW CHUNKS ---
        for (Vector3 pos : targetRenderedChunks) {
            if (generatedChunks.contains(pos) && !renderedChunks.contains(pos)) {
                Chunk chunk = world.getChunk((int)pos.x, (int)pos.y, (int)pos.z);

                ChunkMeshGenerationOutputData meshOutput = inputData.meshGenerator.execute(
                    new ChunkMeshGenerationInputData(world, chunk)
                );

                if (meshOutput.getMeshData() != null) {
                    inputData.output.onChunkMeshReady(pos, meshOutput.getMeshData());
                    renderedChunks.add(pos);
                }
            }
        }
    }

    private boolean isWithinRadius(int chunkX, int chunkZ, int centerX, int centerZ, int radius) {
        long dx = chunkX - centerX;
        long dz = chunkZ - centerZ;
        return (dx * dx + dz * dz) <= (long) radius * radius;
    }
}
