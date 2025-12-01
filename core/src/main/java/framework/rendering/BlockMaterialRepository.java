package framework.rendering;

import com.badlogic.gdx.graphics.g3d.Material;
import domain.Block;

// Another Port for rendering-specific data
public interface BlockMaterialRepository {
    Material getMaterial(Block block);
}

