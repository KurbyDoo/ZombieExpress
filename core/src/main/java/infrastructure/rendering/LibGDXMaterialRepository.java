package infrastructure.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import domain.entities.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * RESEARCH NOTE: MATERIAL REPOSITORY - CURRENT vs FUTURE
 * =======================================================
 * 
 * CURRENT IMPLEMENTATION:
 * - Returns Material with ColorAttribute.Diffuse (solid colors)
 * - Simple mapping: Block ID -> Color
 * - No texture support
 * 
 * TEXTURE-BASED IMPLEMENTATION APPROACHES:
 * 
 * APPROACH 1: Single Texture Atlas (RECOMMENDED)
 * -----------------------------------------------
 * - Load one texture atlas containing all block textures in a grid
 * - Material uses TextureAttribute with the atlas
 * - UV coordinates in mesh determine which part of atlas is used
 * 
 * Pros:
 * - Best performance (single texture binding)
 * - Standard voxel game approach
 * - Easy to add new block types (update atlas)
 * 
 * Cons:
 * - Atlas must be pre-built
 * - Limited by atlas size (e.g., 256x256 = 16x16 blocks at 16x16px each)
 * - Changing textures requires atlas regeneration
 * 
 * Implementation:
 * ```java
 * private Texture textureAtlas;
 * private Material atlasMaterial;
 * 
 * public LibGDXMaterialRepository() {
 *     textureAtlas = new Texture(Gdx.files.internal("textures/blocks_atlas.png"));
 *     atlasMaterial = new Material(
 *         TextureAttribute.createDiffuse(textureAtlas),
 *         ColorAttribute.createSpecular(1, 1, 1, 1) // Optional
 *     );
 * }
 * 
 * @Override
 * public Material getMaterial(Block block) {
 *     // All blocks use same atlas material
 *     // UV coords in mesh determine which texture appears
 *     return atlasMaterial;
 * }
 * ```
 * 
 * APPROACH 2: Per-Block Textures
 * -------------------------------
 * - Each block type has its own texture file
 * - Material per block with its specific texture
 * 
 * Pros:
 * - Simple to understand and implement
 * - Easy to swap individual textures
 * - No atlas management
 * 
 * Cons:
 * - Many texture bindings (performance impact)
 * - More materials to manage
 * - Doesn't scale well (100 blocks = 100 textures)
 * 
 * Implementation:
 * ```java
 * private final Map<Short, Material> materials = new HashMap<>();
 * 
 * public LibGDXMaterialRepository() {
 *     Texture grassTexture = new Texture(Gdx.files.internal("textures/grass.png"));
 *     materials.put((short)1, new Material(TextureAttribute.createDiffuse(grassTexture)));
 *     
 *     Texture dirtTexture = new Texture(Gdx.files.internal("textures/dirt.png"));
 *     materials.put((short)2, new Material(TextureAttribute.createDiffuse(dirtTexture)));
 *     // ... etc
 * }
 * ```
 * 
 * APPROACH 3: Face-Aware Materials
 * ---------------------------------
 * - Some blocks need different textures per face (grass: top vs side)
 * - Requires repository to know about face direction
 * 
 * Pros:
 * - Supports complex blocks (grass, logs, furnaces)
 * - More realistic rendering
 * 
 * Cons:
 * - More complex API (need to pass face direction)
 * - Requires mesh builder to group faces by texture
 * - Still needs atlas for performance
 * 
 * Implementation:
 * ```java
 * public enum BlockFace { TOP, BOTTOM, NORTH, SOUTH, EAST, WEST }
 * 
 * public Material getMaterial(Block block, BlockFace face) {
 *     // Return different texture coords based on face
 *     // Still uses atlas, but calculates different UV regions
 * }
 * ```
 * 
 * CLEAN ARCHITECTURE CONSIDERATIONS:
 * ===================================
 * - BlockMaterialRepository is a PORT (interface in use_cases/ports)
 * - This class is an ADAPTER (infrastructure layer)
 * - Domain layer (Block) should NOT know about rendering details
 * - Material selection logic belongs in infrastructure, not domain
 * 
 * SOLID PRINCIPLES APPLICATION:
 * ==============================
 * - Single Responsibility: This class only manages material creation
 * - Open/Closed: New block types can be added without modifying interface
 * - Liskov Substitution: Any BlockMaterialRepository implementation works
 * - Interface Segregation: Clean interface with one method
 * - Dependency Inversion: Domain depends on interface, not implementation
 * 
 * RECOMMENDED MIGRATION PATH:
 * ===========================
 * 1. Create TextureAtlasRepository (new class)
 * 2. Modify this class to use atlas-based materials
 * 3. Keep interface unchanged (getMaterial(Block))
 * 4. Let mesh builder handle UV coordinate calculation
 * 5. Optionally extend interface for face-aware materials later
 */
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
