/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Entity Pattern: Represents a chunk of the game world with unique identity.
 * - Aggregate Pattern: Chunk aggregates block data and entity references.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers (use cases, interface adapters, frameworks).
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [WARN] SRP: Chunk manages blocks, height maps, AND entity IDs. Consider separating
 *   entity tracking into a separate component (ChunkEntityRegistry).
 * - [PASS] OCP: Can be extended without modification.
 * - [N/A] LSP: No inheritance hierarchy.
 * - [N/A] ISP: No interfaces implemented.
 * - [N/A] DIP: No dependencies to invert.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Constants use UPPER_SNAKE_CASE.
 * - [MINOR] Missing Javadoc documentation.
 * - [PASS] Uses Java collections appropriately.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level and method-level Javadoc comments.
 * - [PASS] Proper use of final keyword for immutable fields.
 */
package domain;

import java.util.HashSet;
import java.util.Set;

public class Chunk {
    public static final int CHUNK_SIZE = 16;

    private short[][][] blocks = new short[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
    private int[][] heightMap = new int[CHUNK_SIZE][CHUNK_SIZE];
    private int maxBlockHeight;
    private int minBlockHeight;
    private final GamePosition chunkCoordinates;
    private Set<Integer> entityIds;

    public Chunk(GamePosition pos) {
        chunkCoordinates = pos;
        entityIds = new HashSet<>();
        maxBlockHeight = 0;
        minBlockHeight = CHUNK_SIZE * CHUNK_SIZE;
    }

    public Chunk(int chunkX, int chunkY, int chunkZ) {
        this(new GamePosition(chunkX, chunkY, chunkZ));
    }

    public GamePosition getPosition() {
        return new GamePosition(chunkCoordinates);
    }

    public short getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    public void setBlock(int x, int y, int z, Block block) {
        blocks[x][y][z] = block.getId();
    }

    public void setHeight(int x, int y, int z) {
        heightMap[x][z] = y;
        maxBlockHeight = Math.max(maxBlockHeight, y);
        minBlockHeight = Math.min(minBlockHeight, y);
    }

    public int getHeight(int x, int z) {
        return heightMap[x][z];
    }

    public int getMaxBlockHeight() {
        return maxBlockHeight;
    }

    public int getMinBlockHeight() {
        return minBlockHeight;
    }

    public int getChunkX() {
        return (int) chunkCoordinates.x;
    }

    public int getChunkY() {
        return (int) chunkCoordinates.y;
    }

    public int getChunkZ() {
        return (int) chunkCoordinates.z;
    }

    public int getChunkWorldX() {
        return getChunkX() * CHUNK_SIZE;
    }

    public int getChunkWorldY() {
        return getChunkY() * CHUNK_SIZE;
    }

    public int getChunkWorldZ() {
        return getChunkZ() * CHUNK_SIZE;
    }

    public GamePosition getWorldPosition() {
        return new GamePosition(getChunkWorldX(), getChunkWorldY(), getChunkWorldZ());
    }

    public void addEntity(int id) {
        entityIds.add(id);
    }

    public void removeEntity(int id) {
        entityIds.remove(id);
    }

    public Set<Integer> getEntityIds() {
        return entityIds;
    }
}
