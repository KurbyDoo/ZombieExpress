package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import application.use_cases.ChunkRadius.ChunkRadiusManagerOutputBoundary;
import domain.entities.Chunk;
import domain.entities.World;

import java.util.HashMap;
import java.util.Map;

public class ChunkRenderer implements ChunkRadiusManagerOutputBoundary {

    private final GameMeshBuilder meshBuilder;
    private final ObjectRenderer objectRenderer;
    private final World world;

    private final Map<Vector3, ModelInstance> instances = new HashMap<>();

    public ChunkRenderer(GameMeshBuilder meshBuilder, ObjectRenderer objectRenderer, World world) {
        this.meshBuilder = meshBuilder;
        this.objectRenderer = objectRenderer;
        this.world = world;
    }

    @Override
    public void onChunkCreated(Vector3 pos) {
        Chunk chunk = world.getChunk((int) pos.x, (int) pos.y, (int) pos.z);
        if (chunk == null) return; // safety check


        ModelInstance instance = meshBuilder.build(chunk);

        instances.put(new Vector3(pos), instance);
    }

    @Override
    public void onChunkRemoved(Vector3 pos) {
        ModelInstance removed = instances.remove(pos);
        if (removed != null && removed.model != null) {
            removed.model.dispose();
        }
    }

    public void render() {
        for (ModelInstance inst : instances.values()) {
            objectRenderer.modelBatch.render(inst, objectRenderer.environment);
        }
    }
}
