package infrastructure.rendering;

import domain.entities.Chunk;

import infrastructure.rendering.ChunkMeshData;
import infrastructure.rendering.ModelGeneratorFacade;
import infrastructure.rendering.ObjectRenderer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// TODO: Eventually make this multithreaded
public class ChunkLoader {
    private BlockingQueue<Chunk> chunksToLoad;
    private final ModelGeneratorFacade meshBuilder;
    private final ObjectRenderer objectRenderer;

    private final int BUFFER_SIZE = 32;

    public ChunkLoader(ModelGeneratorFacade meshBuilder, ObjectRenderer objectRenderer) {
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


                ChunkMeshData chunkMesh = (ChunkMeshData) meshBuilder.buildModel(chunk);
                if (chunkMesh != null) {
                    objectRenderer.add(chunkMesh);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
