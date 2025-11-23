package domain;

import com.badlogic.gdx.math.Vector3;

import java.util.HashSet;
import java.util.Set;

public class Chunk {
    public static final int CHUNK_SIZE = 16;

    private short[][][] blocks = new short[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
    private final Vector3 chunkCoordinates;
    private Set<Integer> entityIds;

    public Chunk(Vector3 pos) {
        chunkCoordinates = pos;
        entityIds = new HashSet<>();
    }

    public Chunk(int chunkX, int chunkY, int chunkZ) {
        this(new Vector3(chunkX, chunkY, chunkZ));
    }

    public Vector3 getPosition() {
        return new Vector3(chunkCoordinates);
    }

    public short getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    public void setBlock(int x, int y, int z, Block block) {
        blocks[x][y][z] = block.getId();
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

    public Vector3 getWorldPosition() {
        return new Vector3(getChunkWorldX(), getChunkWorldY(), getChunkWorldZ());
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
