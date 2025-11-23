package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import physics.GameMesh;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IdToMeshStorage implements MeshStorage {
    private final Map<Integer, GameMesh> meshStorage;
    private final Set<GameMesh> meshesToLoad;
    private final Set<GameMesh> meshesToUnload;

    public IdToMeshStorage() {
        meshStorage = new HashMap<>();
        meshesToLoad = new HashSet<>();
        meshesToUnload = new HashSet<>();
    }

    public void addMesh(int id, GameMesh mesh) {
        if (mesh == null || meshStorage.containsKey(id)) return;
        meshesToLoad.add(mesh);
        meshStorage.put(id, mesh);
    }

    public boolean hasMesh(int id) {
        return meshStorage.containsKey(id);
    }

    public GameMesh getMesh(int id) {
        return meshStorage.getOrDefault(id, null);
    }

    public void removeMesh(int id) {
        GameMesh mesh = meshStorage.get(id);
        if (mesh != null) {
            meshesToUnload.add(mesh);
        }
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
    public void getRenderables(Array<Renderable> array, Pool<Renderable> pool) {
        for (GameMesh mesh : meshStorage.values()) {
            mesh.getScene().modelInstance.getRenderables(array, pool);
        }
    }

    @Override
    public Collection<GameMesh> getAllMeshes() {
        return meshStorage.values();
    }
}
