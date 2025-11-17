package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Material;
import domain.entities.Block;

// Another Port for rendering-specific data
public interface BlockMaterialRepository {
    Material getMaterial(Block block);
}

