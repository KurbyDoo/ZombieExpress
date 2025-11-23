package infrastructure.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import domain.Block;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;

import java.util.HashMap;
import java.util.Map;

public class LibGDXMaterialRepository implements BlockMaterialRepository {
    private final Map<Short, Material> materials = new HashMap<>();

    // TODO: Fix colours
    public LibGDXMaterialRepository() {
        // Grass (Green)
        materials.put((short)1, createPBRMaterial(new Color(0.2f, 0.8f, 0.2f, 1f)));

        // Dirt (Brown)
        materials.put((short)2, createPBRMaterial(new Color(0.6f, 0.4f, 0.2f, 1f)));

        // Stone (Grey)
        materials.put((short)3, createPBRMaterial(new Color(0.5f, 0.5f, 0.5f, 1f)));
    }

    /**
     * Helper to create a matte (non-shiny) PBR material.
     */
    private Material createPBRMaterial(Color color) {
        Material mat = new Material();

        // 1. The Color (Albedo) - Critical for PBR
        mat.set(PBRColorAttribute.createBaseColorFactor(color));

        // 2. Metallic (0 = dielectric/plastic/wood/dirt, 1 = metal)
        mat.set(PBRFloatAttribute.createMetallic(0.0f));

        // 3. Roughness (0 = smooth mirror, 1 = matte/rough)
        // Set to 1.0f for dirt/stone so it doesn't look like wet plastic
        mat.set(PBRFloatAttribute.createRoughness(1.0f));

        return mat;
    }

    @Override
    public Material getMaterial(Block block) {
        // Default (Magenta) if block not found
        return materials.getOrDefault(block.getId(), createPBRMaterial(Color.MAGENTA));
    }
}
