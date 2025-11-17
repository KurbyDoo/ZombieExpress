package io.github.testlibgdx;

import domain.entities.Chunk;

import infrastructure.rendering.ChunkMeshData;
import infrastructure.rendering.GameMeshBuilder;
import infrastructure.rendering.ObjectRenderer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// TODO: Eventually make this multithreaded
public class ChunkLoader {
    private BlockingQueue<Chunk> chunksToLoad;
    private final GameMeshBuilder meshBuilder;
    private final ObjectRenderer objectRenderer;

    private final int BUFFER_SIZE = 2;

    public ChunkLoader(GameMeshBuilder meshBuilder, ObjectRenderer objectRenderer) {
        this.meshBuilder = meshBuilder;
        this.objectRenderer = objectRenderer;
        chunksToLoad = new LinkedBlockingQueue<>();
    }

    public void addChunkToLoad(Chunk chunk) {
        chunksToLoad.add(chunk);
    }

    public void loadChunks() {
        try {
            Chunk chunk;
            for (int i = 0; i < BUFFER_SIZE && ((chunk = chunksToLoad.poll()) != null); i++) {

//                ChunkMeshData chunkMesh;
//                chunkMesh = meshBuilder.build(chunk);
//                objectRenderer.add(chunkMesh);

                ChunkMeshData chunkMesh = meshBuilder.build(chunk);
                if (chunkMesh != null) {
                    objectRenderer.add(chunkMesh);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
