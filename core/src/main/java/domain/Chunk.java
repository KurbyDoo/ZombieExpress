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
