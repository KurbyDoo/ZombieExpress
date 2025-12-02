package data_access;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import framework.physics.GameMesh;
import framework.rendering.MeshStorage;

import java.util.HashMap;
import java.util.Map;

public class MockMeshStorage implements MeshStorage {

    Map<Integer, GameMesh> meshes = new HashMap<>();

    @Override
    public void addMesh(int id, GameMesh mesh){
        meshes.put(id, mesh);
    }

    @Override
    public boolean hasMesh(int id){
        return meshes.containsKey(id);
    }

    @Override
    public void removeMesh(int id){
        meshes.remove(id);
    }

    @Override
    public GameMesh getMesh(int id){
        return meshes.getOrDefault(id, null);
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {

    }
}
