package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import physics.CollisionHandler;
import physics.GameMesh;

import java.util.*;

public class IdToMeshStorage implements MeshStorage {
    private final Map<Integer, GameMesh> meshStorage;
    private final CollisionHandler collisionHandler;

    public IdToMeshStorage(CollisionHandler collisionHandler) {
        meshStorage = new HashMap<>();
        this.collisionHandler = collisionHandler;
    }

    public void addMesh(int id, GameMesh mesh) {
        if (meshStorage.containsKey(id)) return;
        meshStorage.put(id, mesh);
        collisionHandler.add(mesh);
    }

    public boolean hasMesh(int id) {
        return meshStorage.containsKey(id);
    }

    public GameMesh getMesh(int id) {
        return meshStorage.getOrDefault(id, null);
    }

    public void removeMesh(int id) {
        if (!meshStorage.containsKey(id)) {
            System.out.println("ID " + id + " already removed!");
            return;
        }
        collisionHandler.remove(meshStorage.get(id).getBody());
        meshStorage.remove(id);
    }

    @Override
    public void getRenderables(Array<Renderable> array, Pool<Renderable> pool) {
        for (GameMesh mesh : meshStorage.values()) {
            if (mesh.getScene() != null) {
                mesh.getScene().modelInstance.getRenderables(array, pool);
            }

            if (mesh.isShowHitbox() && mesh.getHitboxInstance() != null) {
                mesh.getHitboxInstance().getRenderables(array, pool);
            }
        }
    }
}
