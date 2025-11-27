package application.use_cases.render_radius;

import domain.Chunk;
import domain.World;
import com.badlogic.gdx.math.Vector3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RenderRadiusManagerInteractor implements RenderRadiusManagerInputBoundary {
    // --- WORLD BOUNDARY CONSTANTS ---
    private final int MAX_WORLD_Z_CHUNKS = 8;
    private final int MIN_WORLD_Z_CHUNKS = -8;
    // TODO: This needs to be synced globally with chunk generation
    private final int MAX_WORLD_X_CHUNKS;
    private final int MIN_WORLD_X_CHUNKS = -8;
    private final int MAX_WORLD_Y_CHUNKS = 8;
    private final int MIN_WORLD_Y_CHUNKS = 0;
    private final int Y_VIEW_RANGE = 3;

    private int lastChunkX = Integer.MAX_VALUE;
    private int lastChunkZ = Integer.MAX_VALUE;

    // Tracks chunks currently in the world
    private final Set<Vector3> renderedChunks = new HashSet<>();

    public RenderRadiusManagerInteractor(World world) {
        MAX_WORLD_X_CHUNKS = world.getWorldDepthChunks() + 12;
    }

    @Override
    public RenderRadiusOutputData execute(RenderRadiusManagerInputData inputData) {
        World world = inputData.getWorld();
        Vector3 playerPos = inputData.getPlayerPosition();
        RenderRadiusOutputData result = new RenderRadiusOutputData();

        int currentChunkX = (int) Math.floor(playerPos.x / Chunk.CHUNK_SIZE);
        int currentChunkZ = (int) Math.floor(playerPos.z / Chunk.CHUNK_SIZE);

        // Only update when we move to a new chunk.
        if (currentChunkX == lastChunkX && currentChunkZ == lastChunkZ) {
            result.getChunksToUpdate().addAll(renderedChunks);
            return result;
        }

        lastChunkX = currentChunkX;
        lastChunkZ = currentChunkZ;

        final int RENDER_RADIUS = inputData.getRenderRadius();
        final int GENERATION_RADIUS = RENDER_RADIUS + 1;
        Set<Vector3> targetGeneratedChunks = getTargetGeneratedChunks(currentChunkX, currentChunkZ, GENERATION_RADIUS);
        Set<Vector3> targetRenderedChunks = getTargetRenderedChunks(currentChunkX, currentChunkZ, RENDER_RADIUS);

        // --- Unload Chunks ---
        Iterator<Vector3> renderedIterator = renderedChunks.iterator();
        while (renderedIterator.hasNext()) {
            Vector3 pos = renderedIterator.next();
            if (targetRenderedChunks.contains(pos)) continue;
            result.getChunksToUnload().add(pos);
            renderedIterator.remove();
        }

        result.getChunksToUpdate().clear();
        result.getChunksToUpdate().addAll(renderedChunks);

        // --- GENERATE NEW CHUNKS ---
        for (Vector3 pos : targetGeneratedChunks) {
            if (world.hasChunk(pos)) continue;
            result.getChunksToGenerate().add(pos);
        }

        // --- MESH NEW CHUNKS ---
        for (Vector3 pos : targetRenderedChunks) {
            if (renderedChunks.contains(pos)) continue;
            renderedChunks.add(pos);
            result.getChunksToLoad().add(pos);
        }

        return result;
    }

    private Set<Vector3> getTargetGeneratedChunks(int currentChunkX, int currentChunkZ, int generationRadius) {
        int zStart = Math.max(currentChunkZ - generationRadius, MIN_WORLD_Z_CHUNKS);
        int zEnd = Math.min(currentChunkZ + generationRadius, MAX_WORLD_Z_CHUNKS);

        int xStart = Math.max(currentChunkX - generationRadius, MIN_WORLD_X_CHUNKS);
        int xEnd = Math.min(currentChunkX + generationRadius, MAX_WORLD_X_CHUNKS);
        Set<Vector3> targetGeneratedChunks = new HashSet<>();
        for (int targetX = xStart; targetX <= xEnd; targetX++) {
            for (int targetZ = zStart; targetZ <= zEnd; targetZ++) {
                for (int y = -Y_VIEW_RANGE; y <= Y_VIEW_RANGE; y++) {
                    if (!isWithinWorldBounds(targetX, y, targetZ)) continue;
                    Vector3 chunkPos = new Vector3(targetX, y, targetZ);

                    targetGeneratedChunks.add(chunkPos);
                }
            }
        }
        return targetGeneratedChunks;
    }

    private Set<Vector3> getTargetRenderedChunks(int currentChunkX, int currentChunkZ, int renderRadius) {
        int zStart = Math.max(currentChunkZ - renderRadius, MIN_WORLD_Z_CHUNKS);
        int zEnd = Math.min(currentChunkZ + renderRadius, MAX_WORLD_Z_CHUNKS);

        int xStart = Math.max(currentChunkX - renderRadius, MIN_WORLD_X_CHUNKS);
        int xEnd = Math.min(currentChunkX + renderRadius, MAX_WORLD_X_CHUNKS);
        Set<Vector3> targetRenderedChunks = new HashSet<>();
        for (int targetX = xStart; targetX <= xEnd; targetX++) {
            for (int targetZ = zStart; targetZ <= zEnd; targetZ++) {
                for (int y = -Y_VIEW_RANGE; y <= Y_VIEW_RANGE; y++) {
                    if (!isWithinWorldBounds(targetX, y, targetZ)) continue;
                    Vector3 chunkPos = new Vector3(targetX, y, targetZ);

                    // Only mesh if within the smaller render radius
                    if (isWithinRadius(targetX, targetZ, currentChunkX, currentChunkZ, renderRadius)) {
                        targetRenderedChunks.add(chunkPos);
                    }
                }
            }
        }
        return targetRenderedChunks;
    }

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

    private boolean isWithinRadius(int chunkX, int chunkZ, int centerX, int centerZ, int radius) {
        long dx = chunkX - centerX;
        long dz = chunkZ - centerZ;
        return (dx * dx + dz * dz) <= (long) radius * radius;
    }
}
