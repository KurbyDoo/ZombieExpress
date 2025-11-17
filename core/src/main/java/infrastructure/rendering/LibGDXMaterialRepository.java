package infrastructure.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import domain.entities.Block;

import java.util.HashMap;
import java.util.Map;

public class LibGDXMaterialRepository implements BlockMaterialRepository {
    private final Map<Short, Material> materials = new HashMap<>();

    // TODO: We will update this repository to read from json
    public LibGDXMaterialRepository() {
        materials.put((short)1, new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.8f, 0.2f, 1f)))); // Grass
        materials.put((short)2, new Material(ColorAttribute.createDiffuse(new Color(0.6f, 0.4f, 0.2f, 1f)))); // Dirt
        materials.put((short)3, new Material(ColorAttribute.createDiffuse(new Color(0.5f, 0.5f, 0.5f, 1f)))); // Stone
    }

    @Override
    public Material getMaterial(Block block) {
        return materials.getOrDefault(block.getId(), new Material(ColorAttribute.createDiffuse(Color.MAGENTA)));
    }
}
