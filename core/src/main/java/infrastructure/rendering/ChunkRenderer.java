package infrastructure.rendering;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.scene3d.scene.Scene;
import application.use_cases.render_radius.RenderRadiusManagerOutputBoundary;
import physics.GameMesh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkRenderer implements RenderRadiusManagerOutputBoundary, Disposable {
    private final ObjectRenderer objectRenderer;

    private final Map<Vector3, GameMesh> activeChunks;
    private final Map<Vector3, ChunkMeshData> rawDataReferences;

    public ChunkRenderer(ObjectRenderer objectRenderer) {
        this.objectRenderer = objectRenderer;
        this.activeChunks = new ConcurrentHashMap<>();
        this.rawDataReferences = new ConcurrentHashMap<>();
    }

    @Override
    public void onChunkCreated(Vector3 chunkPos) { }

    @Override
    public void onChunkMeshReady(Vector3 chunkPos, ChunkMeshData meshData) {
        if (meshData == null) return;

        removeChunk(chunkPos);

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

    @Override
    public void onChunkRemoved(Vector3 chunkPos) {
        removeChunk(chunkPos);
    }

    private void removeChunk(Vector3 chunkPos) {
        GameMesh mesh = activeChunks.remove(chunkPos);
        rawDataReferences.remove(chunkPos);

        if (mesh != null) {
            objectRenderer.remove(mesh);
        }
    }

    @Override
    public void dispose() {
        for (Vector3 key : activeChunks.keySet()) {
            removeChunk(key);
        }
    }
}
