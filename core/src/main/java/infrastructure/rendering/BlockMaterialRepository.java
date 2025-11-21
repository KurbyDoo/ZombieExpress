package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Material;
import domain.entities.Block;

/**
 * RESEARCH NOTE: BLOCK MATERIAL REPOSITORY - PORT PATTERN ANALYSIS
 * =================================================================
 * 
 * CURRENT LOCATION: infrastructure.rendering (SHOULD BE: application.use_cases.ports)
 * 
 * CLEAN ARCHITECTURE ISSUE:
 * This interface is currently in the infrastructure layer, but it's used by
 * the use case layer (ChunkMeshGenerationInteractor). This violates dependency rules.
 * 
 * CORRECT ARCHITECTURE:
 * ```
 * application/use_cases/ports/BlockMaterialRepository.java  (interface - PORT)
 * infrastructure/rendering/LibGDXMaterialRepository.java    (implementation - ADAPTER)
 * ```
 * 
 * WHY THIS MATTERS:
 * - Use cases should depend on abstractions (interfaces) in their own layer
 * - Infrastructure provides concrete implementations
 * - Dependency direction: Infrastructure → Application → Domain (never reversed)
 * 
 * DEPENDENCY INVERSION PRINCIPLE (DIP):
 * "High-level modules should not depend on low-level modules. 
 *  Both should depend on abstractions."
 * 
 * Current (WRONG):
 *   ChunkMeshGenerationInteractor (use case) 
 *     → depends on → 
 *   BlockMaterialRepository (infrastructure interface)
 * 
 * Correct (RIGHT):
 *   ChunkMeshGenerationInteractor (use case) 
 *     → depends on → 
 *   BlockMaterialRepository (use case port)
 *     ← implemented by ←
 *   LibGDXMaterialRepository (infrastructure adapter)
 * 
 * REFACTORING RECOMMENDATION:
 * 1. Move this interface to application/use_cases/ports/
 * 2. Keep LibGDXMaterialRepository in infrastructure/rendering/
 * 3. Update import statements in ChunkMeshGenerationInteractor
 * 
 * TEXTURE SUPPORT EXTENSIONS:
 * When adding texture support, consider these interface options:
 * 
 * OPTION 1: Keep Current Interface (Simple)
 * ------------------------------------------
 * ```java
 * public interface BlockMaterialRepository {
 *     Material getMaterial(Block block);
 *     // Material internally has TextureAttribute instead of ColorAttribute
 *     // UV mapping handled in mesh generation, not here
 * }
 * ```
 * Pros: No interface changes, backward compatible
 * Cons: Limited flexibility for face-specific textures
 * 
 * OPTION 2: Face-Aware Interface (Flexible)
 * ------------------------------------------
 * ```java
 * public enum BlockFace { TOP, BOTTOM, NORTH, SOUTH, EAST, WEST }
 * 
 * public interface BlockMaterialRepository {
 *     Material getMaterial(Block block);  // Backward compatible
 *     Material getMaterial(Block block, BlockFace face);  // New method
 *     int getTextureIndex(Block block, BlockFace face);  // For UV calculation
 * }
 * ```
 * Pros: Supports complex blocks (grass, logs), maintains compatibility
 * Cons: More complex implementation
 * 
 * OPTION 3: Texture Atlas Provider (Separated Concerns)
 * ------------------------------------------------------
 * ```java
 * // Keep this interface simple for materials
 * public interface BlockMaterialRepository {
 *     Material getMaterial(Block block);
 * }
 * 
 * // New interface for texture mapping
 * public interface TextureAtlasProvider {
 *     int getTextureIndex(Block block, BlockFace face);
 *     Texture getAtlasTexture();
 *     Vector2 getUVSize();  // Size of one texture in atlas (e.g., 1/16)
 * }
 * ```
 * Pros: Single Responsibility Principle, cleaner separation
 * Cons: Two interfaces to implement and inject
 * 
 * RECOMMENDED FOR TEXTURE SUPPORT:
 * Start with Option 1 for quick implementation, evolve to Option 3 for clean architecture.
 * 
 * This follows the OPEN/CLOSED PRINCIPLE:
 * - Open for extension (new methods for textures)
 * - Closed for modification (existing getMaterial still works)
 */
// Another Port for rendering-specific data
public interface BlockMaterialRepository {
    Material getMaterial(Block block);
}

