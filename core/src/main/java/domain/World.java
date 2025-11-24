package domain;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class World {
    private final int worldDepthChunks = 20;
    private HashMap<Vector3, Chunk> chunks;

    public World() {
        chunks = new HashMap<>();
    }

    public HashMap<Vector3, Chunk> getChunks() {
        return chunks;
    }

    public boolean addChunk(Vector3 pos, Chunk chunk) {
        if (chunks.containsKey(pos)) return false;
        chunks.put(pos, chunk);
        return true;
    }

    public short getBlock(int x, int y, int z) {
        int localX = ((x % Chunk.CHUNK_SIZE) + Chunk.CHUNK_SIZE) % Chunk.CHUNK_SIZE;
        int localY = ((y % Chunk.CHUNK_SIZE) + Chunk.CHUNK_SIZE) % Chunk.CHUNK_SIZE;
        int localZ = ((z % Chunk.CHUNK_SIZE) + Chunk.CHUNK_SIZE) % Chunk.CHUNK_SIZE;

        int chunkX = Math.floorDiv(x, Chunk.CHUNK_SIZE);
        int chunkY = Math.floorDiv(y, Chunk.CHUNK_SIZE);
        int chunkZ = Math.floorDiv(z, Chunk.CHUNK_SIZE);
        Vector3 chunkVector = new Vector3(chunkX, chunkY, chunkZ);

        if (chunks.containsKey(chunkVector)) {
            return chunks.get(chunkVector).getBlock(localX, localY, localZ);
        } else {
            return 3; // TODO: Change this somehow to not be hardcoded
        }
    }

    public int getWorldDepthChunks() {
        return worldDepthChunks;
    }

    public boolean hasChunk(int x, int y, int z) {
        return chunks.containsKey(new Vector3(x, y, z));
    }

    public boolean hasChunk(Vector3 pos) {
        return chunks.containsKey(pos);
    }

    public void removeChunk(int x, int y, int z) {
        chunks.remove(new Vector3(x, y, z));
    }

    public Set<Vector3> getChunkPositions() {
        return chunks.keySet();
    }

    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        return chunks.get(new Vector3(chunkX, chunkY, chunkZ));
    }

    public Chunk getChunk(Vector3 pos) {
        return chunks.get(pos);
    }

    public List<Integer> getEntitiesInChunks(Set<Vector3> activeChunks) {
        ArrayList<Integer> result = new ArrayList<>();
        for (Vector3 pos : activeChunks) {
            result.addAll(getChunk(pos).getEntityIds());
        }
        return result;
    }

    public Chunk getChunkFromWorldPos(Vector3 position) {
        int chunkX = Math.floorDiv((int) position.x, Chunk.CHUNK_SIZE);
        int chunkY = Math.floorDiv((int) position.y, Chunk.CHUNK_SIZE);
        int chunkZ = Math.floorDiv((int) position.z, Chunk.CHUNK_SIZE);
        return getChunk(new Vector3(chunkX, chunkY, chunkZ));
    }
}
