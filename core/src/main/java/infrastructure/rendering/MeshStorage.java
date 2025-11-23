package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import physics.GameMesh;

import java.util.Set;

public interface MeshStorage extends RenderableProvider {
    void addMesh(int id, GameMesh mesh);
    boolean hasMesh(int id);
    void removeMesh(int id);
    GameMesh getMesh(int id);
    Set<GameMesh> getMeshesToLoad();
    Set<GameMesh> getMeshesToUnload();
}
