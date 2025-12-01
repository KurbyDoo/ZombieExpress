/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Infrastructure/Rendering)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package infrastructure.rendering;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import domain.Chunk;
import domain.GamePosition;
import net.mgsx.gltf.scene3d.scene.Scene;
import physics.GameMesh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkRenderer implements Disposable {
    private final ObjectRenderer objectRenderer;
    private final ChunkMeshGenerator chunkMeshGenerator;
    private final EntityRenderer entityRenderer;

    private final Map<GamePosition, GameMesh> activeChunks;
    private final Map<GamePosition, ChunkMeshData> rawDataReferences;


    public ChunkRenderer(ObjectRenderer objectRenderer, ChunkMeshGenerator chunkMeshGenerator, EntityRenderer entityRenderer) {
        this.objectRenderer = objectRenderer;
        this.chunkMeshGenerator = chunkMeshGenerator;
        this.entityRenderer = entityRenderer;

        this.activeChunks = new ConcurrentHashMap<>();
        this.rawDataReferences = new ConcurrentHashMap<>();
    }

    public void loadChunk(GamePosition chunkPosition, Chunk chunk) {
        ChunkMeshData meshData = chunkMeshGenerator.createMesh(chunk);

        if (meshData != null) {
            addChunkToRenderer(chunkPosition, meshData);
        }

        for (int id : chunk.getEntityIds()) {
            entityRenderer.loadEntity(id);
        }
    }

    private void addChunkToRenderer(GamePosition chunkPos, ChunkMeshData meshData) {
        if (meshData == null) return;

        removeChunkFromRenderer(chunkPos);

        Scene scene = new Scene(meshData.getModel());

        btRigidBody.btRigidBodyConstructionInfo info =
            new btRigidBody.btRigidBodyConstructionInfo(0, null, meshData.getShape(), Vector3.Zero);
        btRigidBody body = new btRigidBody(info);
        info.dispose();

        int dummyId = chunkPos.hashCode();
        GameMesh gameMesh = new GameMesh(dummyId, scene, body, null);
        gameMesh.setStatic(true);
        gameMesh.setChunkMeshData(meshData);

        activeChunks.put(chunkPos, gameMesh);
        rawDataReferences.put(chunkPos, meshData);

        objectRenderer.add(gameMesh);
    }

    public void unloadChunk(GamePosition chunkPos, Chunk chunk) {
        removeChunkFromRenderer(chunkPos);

        for (int id : chunk.getEntityIds()) {
            entityRenderer.unloadEntity(id);
        }
    }

    private void removeChunkFromRenderer(GamePosition chunkPos) {
        GameMesh mesh = activeChunks.remove(chunkPos);
        rawDataReferences.remove(chunkPos);

        if (mesh != null) {
            objectRenderer.remove(mesh);
        }
    }

    @Override
    public void dispose() {
        for (GamePosition key : activeChunks.keySet()) {
            removeChunkFromRenderer(key);
        }
    }
}
