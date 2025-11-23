package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import physics.CollisionHandler;
import physics.GameMesh;

import java.util.*;

public class IdToMeshStorage implements MeshStorage {
    private final Map<Integer, GameMesh> meshStorage;
    private final Set<GameMesh> meshesToLoad;
    private final Set<GameMesh> meshesToUnload;
    private final CollisionHandler collisionHandler;

    public IdToMeshStorage(CollisionHandler collisionHandler) {
        meshStorage = new HashMap<>();
        meshesToLoad = new HashSet<>();
        meshesToUnload = new HashSet<>();
        this.collisionHandler = collisionHandler;
    }

    public void addMesh(int id, GameMesh mesh) {
        if (meshStorage.containsKey(id)) return;
        meshesToLoad.add(mesh);
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
        meshesToUnload.add(meshStorage.get(id));
        collisionHandler.remove(meshStorage.get(id).body);
        meshStorage.remove(id);
    }

    @Override
    public Set<GameMesh> getMeshesToLoad() {
        return meshesToLoad;
    }

    @Override
    public Set<GameMesh> getMeshesToUnload() {
        return meshesToUnload;
    }

    @Override
    public Collection<GameMesh> getAllMeshes() {
        return meshStorage.values();
    }

    @Override
    public void getRenderables(Array<Renderable> array, Pool<Renderable> pool) {
        for (GameMesh mesh : meshStorage.values()) {
            mesh.getScene().modelInstance.getRenderables(array, pool);
        }
    }
}
