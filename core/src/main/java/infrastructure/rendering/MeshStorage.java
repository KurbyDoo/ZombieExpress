/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Infrastructure/Rendering)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import physics.GameMesh;

import java.util.Collection;
import java.util.Set;

public interface MeshStorage extends RenderableProvider {
    void addMesh(int id, GameMesh mesh);
    boolean hasMesh(int id);
    void removeMesh(int id);
    GameMesh getMesh(int id);
}
