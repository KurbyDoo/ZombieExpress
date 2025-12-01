/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Infrastructure/Rendering)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Material;
import domain.Block;

// Another Port for rendering-specific data
public interface BlockMaterialRepository {
    Material getMaterial(Block block);
}

