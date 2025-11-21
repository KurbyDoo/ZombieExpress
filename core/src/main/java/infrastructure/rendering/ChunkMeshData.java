package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import physics.GameMesh;

/**
 * ARCHITECTURAL ASSESSMENT:
 * 
 * ChunkMeshData extends GameMesh, inheriting the coupling between rendering and collision.
 * 
 * CURRENT PURPOSE:
 * - Represents a chunk of voxel terrain
 * - Contains both visual mesh (Model) and collision mesh (btBvhTriangleMeshShape)
 * - Sets 'moving = false' because chunks are static
 * 
 * ISSUES:
 * 1. Inherits GameMesh coupling problem
 * 2. Chunks are static geometry but use same class as dynamic objects
 * 3. Triangle mesh must be carefully disposed (memory leak risk)
 * 
 * RECOMMENDED REFACTORING FOR TWO-SYSTEM ARCHITECTURE:
 * 
 * 1. Separate rendering and collision:
 *    - ChunkRenderData: Contains Model, position, loaded state
 *    - ChunkCollisionData: Contains btBvhTriangleMeshShape, btTriangleMesh, position
 * 
 * 2. Create ChunkEntity in domain layer:
 *    class Chunk {
 *        Vector3 chunkCoordinates;
 *        short[][][] blocks;  // voxel data (already exists)
 *        // No rendering or collision data here
 *    }
 * 
 * 3. Create ChunkUpdater in presentation layer:
 *    - When chunk is loaded, creates BOTH ChunkRenderData and ChunkCollisionData
 *    - Adds ChunkRenderData to RenderingSystem (SceneManager)
 *    - Adds ChunkCollisionData to CollisionSystem
 *    - When chunk is unloaded, removes and disposes both
 * 
 * This allows:
 * - Different LOD levels for rendering (high detail near, low detail far)
 * - Simplified collision meshes (fewer triangles than visual mesh)
 * - Independent memory management
 * - Proper separation of concerns
 */
public class ChunkMeshData extends GameMesh {

    final private btTriangleMesh triangle;

    public ChunkMeshData(Model model, btTriangleMesh triangle, btBvhTriangleMeshShape shape){
        super(model, shape);
        this.triangle = triangle;
        this.moving = false;
    }

    /**
     * WE MUST DISPOSE WHEN DE-RENDERING UNLOADING A CHUNK
     */
    public void dispose() {
        super.dispose();
        triangle.dispose();
    }
}
