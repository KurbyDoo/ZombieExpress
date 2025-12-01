package framework.rendering;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import framework.physics.GameMesh;

public interface MeshStorage extends RenderableProvider {
    void addMesh(int id, GameMesh mesh);
    boolean hasMesh(int id);
    void removeMesh(int id);
    GameMesh getMesh(int id);
}
