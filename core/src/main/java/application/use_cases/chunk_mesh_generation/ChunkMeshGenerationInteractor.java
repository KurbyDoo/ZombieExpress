package application.use_cases.chunk_mesh_generation;

import com.badlogic.gdx.graphics.g3d.Model;
import application.use_cases.ports.BlockRepository;
import domain.entities.Block;
import domain.entities.Chunk;
import domain.entities.World;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import infrastructure.rendering.ChunkMeshData;
import infrastructure.rendering.BlockMaterialRepository;

/**
 * RESEARCH NOTE: CHUNK MESH GENERATION - CURRENT IMPLEMENTATION
 * ==============================================================
 * This interactor generates chunk meshes using LibGDX ModelBuilder with SOLID COLOR materials.
 * 
 * CURRENT APPROACH:
 * - Creates mesh geometry using MeshPartBuilder.rect() for each visible block face
 * - Vertex attributes: Position | Normal (NO UV COORDINATES)
 * - Material: Solid color from LibGDXMaterialRepository (ColorAttribute only)
 * - Output: Model + ModelInstance rendered via ModelBatch
 * 
 * TEXTURE MAPPING RESEARCH:
 * =========================
 * To add texture support, we need to modify this generation process:
 * 
 * APPROACH 1: UV Mapping with Texture Atlas (RECOMMENDED)
 * --------------------------------------------------------
 * Pros:
 * - Most efficient for voxel rendering (single texture, minimal draw calls)
 * - Standard approach in Minecraft-like games
 * - LibGDX ModelBuilder supports UV coordinates via Usage.TextureCoordinates
 * - Compatible with current ModelInstance rendering (can transition gradually)
 * 
 * Cons:
 * - Requires creating/maintaining a texture atlas
 * - UV calculation complexity for each block face
 * - Atlas management for different block types
 * 
 * Implementation:
 * 1. Add Usage.TextureCoordinates to vertex attributes (line 69)
 * 2. Create TextureAtlas with all block textures in a grid
 * 3. Calculate UV coords based on block type and face direction
 * 4. Use TextureAttribute instead of ColorAttribute in materials
 * 5. Modify MeshPartBuilder to include setUVRange() for each face
 * 
 * APPROACH 2: Per-Face Texture with Multi-Material
 * -------------------------------------------------
 * Pros:
 * - Simpler UV mapping (each face uses full 0-1 UV range)
 * - Easier to implement initially
 * - Better for blocks with unique per-face textures (e.g., grass top/side)
 * 
 * Cons:
 * - More draw calls (one per material/texture)
 * - Less performant for large chunks
 * - Doesn't scale well with many block types
 * 
 * Implementation:
 * 1. Create separate MeshPart for each texture
 * 2. Group faces by texture rather than block type
 * 3. Standard UV coords (0,0 to 1,1) for each quad
 * 
 * APPROACH 3: Scene-Based with Procedural GLTF
 * ---------------------------------------------
 * Pros:
 * - Unified rendering with existing Scene system
 * - PBR material support (roughness, metalness, etc.)
 * - Could leverage GLTF ecosystem tools
 * 
 * Cons:
 * - Overhead of GLTF format for procedural meshes
 * - More complex generation pipeline
 * - May need runtime GLTF creation (not well supported)
 * 
 * Implementation:
 * 1. Generate Model with TextureAttribute materials
 * 2. Wrap in Scene object instead of using ModelInstance directly
 * 3. Add to SceneManager instead of ModelBatch
 * 
 * RECOMMENDED SOLUTION:
 * =====================
 * Hybrid Approach: Start with Approach 1 (Texture Atlas) using ModelInstance,
 * then migrate to Scene objects (Approach 3) for unified rendering.
 * 
 * This maintains compatibility with existing collision system (ChunkMeshData)
 * while enabling texture support and eventual SceneManager integration.
 */
public class ChunkMeshGenerationInteractor implements ChunkMeshGenerationInputBoundary {
    private final Vector3 p1 = new Vector3();
    private final Vector3 p2 = new Vector3();
    private final Vector3 p3 = new Vector3();

    private final BlockRepository blockRepository;
    private final BlockMaterialRepository blockMaterialRepository;
    private final Block air;

    public ChunkMeshGenerationInteractor(BlockRepository blockRepository, BlockMaterialRepository blockMaterialRepository) {
        this.blockRepository = blockRepository;
        this.blockMaterialRepository = blockMaterialRepository;
        air = blockRepository.findByName("AIR").orElseThrow();
    }

    @Override
    public ChunkMeshGenerationOutputData execute(ChunkMeshGenerationInputData inputData) {
        World world = inputData.getWorld();
        Chunk chunk = inputData.getChunk();
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        btTriangleMesh triangleMesh = new btTriangleMesh();

        for (Block block : blockRepository.findAll()) {
            if (block.getId() != air.getId()) {
                buildType(world, chunk, triangleMesh, modelBuilder, block);
            }
        }

        btBvhTriangleMeshShape bvhTriangle = null;


        // If the mesh is empty, clean up and return empty null
        ChunkMeshData meshData;
        if (triangleMesh.getNumTriangles() == 0) {
            triangleMesh.dispose();
            modelBuilder.end().dispose();
            meshData = null;
        } else {
            bvhTriangle = new btBvhTriangleMeshShape(triangleMesh, true);
            Model completeModel = modelBuilder.end();
            meshData = new ChunkMeshData(completeModel, triangleMesh, bvhTriangle);
        }

        return new ChunkMeshGenerationOutputData(meshData);
    }


    private void buildType(World world, Chunk chunk, btTriangleMesh triangleMesh, ModelBuilder modelBuilder, Block type) {
        // RESEARCH NOTE: MATERIAL ASSIGNMENT
        // Current: Gets solid color material from repository
        // Future: Should get TextureAttribute-based material with UV mapping
        // The material defines how the mesh surface appears (color OR texture)
        Material material = blockMaterialRepository.getMaterial(type);
        
        // RESEARCH NOTE: VERTEX ATTRIBUTES
        // Current: Usage.Position | Usage.Normal (sufficient for solid colors)
        // Future: Add Usage.TextureCoordinates for UV mapping
        // Example: Usage.Position | Usage.Normal | Usage.TextureCoordinates
        // This tells the GPU what data each vertex contains
        MeshPartBuilder meshBuilder = modelBuilder.part(type.toString(), GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, material);

        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    if (chunk.getBlock(x, y, z) == type.getId()) {
                        buildBlockFaces(world, chunk, triangleMesh, meshBuilder, x, y, z);
                    }
                }
            }
        }
    }

    private void buildBlockFaces(World world, Chunk chunk, btTriangleMesh triangleMesh, MeshPartBuilder meshBuilder, int x, int y, int z) {
        int worldX = x + chunk.getChunkX() * Chunk.CHUNK_SIZE;
        int worldY = y + chunk.getChunkY() * Chunk.CHUNK_SIZE;
        int worldZ = z + chunk.getChunkZ() * Chunk.CHUNK_SIZE;

        boolean removeDuplicateVertices = false;
        
        // RESEARCH NOTE: FACE CULLING & QUAD GENERATION
        // ==============================================
        // Each block face is only generated if adjacent block is air (visible face optimization)
        // Current: meshBuilder.rect() creates quads with position and normal data
        // 
        // TEXTURE MAPPING MODIFICATION:
        // To add UV coordinates, we would need to:
        // 1. Use meshBuilder.setUVRange(u1, v1, u2, v2) before each rect() call
        // 2. Calculate UV coords based on block type and face direction
        // 
        // Example UV calculation for texture atlas (16x16 blocks per atlas):
        //   int textureIndex = getTextureIndex(blockType, faceDirection);
        //   int atlasX = textureIndex % 16;
        //   int atlasY = textureIndex / 16;
        //   float u1 = atlasX / 16.0f;
        //   float v1 = atlasY / 16.0f;
        //   float u2 = (atlasX + 1) / 16.0f;
        //   float v2 = (atlasY + 1) / 16.0f;
        //   meshBuilder.setUVRange(u1, v1, u2, v2);
        // 
        // Different faces may use different textures (e.g., grass: top=grass, side=dirt)
        // This requires a face-aware texture mapping system in BlockMaterialRepository
        
        // Top face (y+)
        if (world.getBlock(worldX, worldY + 1, worldZ) == air.getId()) {
            // FUTURE: meshBuilder.setUVRange(u1, v1, u2, v2) for top texture
            meshBuilder
                .rect(worldX, worldY + 1, worldZ + 1,
                    worldX + 1, worldY + 1, worldZ + 1,
                    worldX + 1, worldY + 1, worldZ,
                    worldX, worldY + 1, worldZ,
                    0, 1, 0);

            // Generate individual TriangleMeshes.
            triangleMesh.addTriangle(
                p1.set(x, y + 1, z + 1),
                p2.set(x + 1, y + 1, z + 1),
                p3.set(x + 1, y + 1, z),
                removeDuplicateVertices);
            triangleMesh.addTriangle(
                p1.set(x, y + 1, z + 1),
                p2.set(x + 1, y + 1, z),
                p3.set(x, y + 1, z),
                removeDuplicateVertices);

        }

        // Bottom face (y-)
        if (world.getBlock(worldX, worldY - 1, worldZ) == air.getId()) {
            meshBuilder
                .rect(worldX, worldY, worldZ,
                    worldX + 1, worldY, worldZ,
                    worldX + 1, worldY, worldZ + 1,
                    worldX, worldY, worldZ + 1,
                    0, -1, 0);

            triangleMesh.addTriangle(
                p1.set(x, y, z),
                p2.set(x + 1, y, z),
                p3.set(x + 1, y, z + 1),
                removeDuplicateVertices);
            triangleMesh.addTriangle(
                p1.set(x, y, z),
                p2.set(x + 1, y, z + 1),
                p3.set(x, y, z + 1),
                removeDuplicateVertices);
        }

        // North face (z+)
        if (world.getBlock(worldX, worldY, worldZ + 1) == air.getId()) {
            meshBuilder
                .rect(worldX, worldY, worldZ + 1,
                    worldX + 1, worldY, worldZ + 1,
                    worldX + 1, worldY + 1, worldZ + 1,
                    worldX, worldY + 1, worldZ + 1,
                    0, 0, 1);

            triangleMesh.addTriangle(
                p1.set(x, y, z + 1),
                p2.set(x + 1, y, z + 1),
                p3.set(x + 1, y + 1, z + 1),
                removeDuplicateVertices
            );
            triangleMesh.addTriangle(
                p1.set(x, y, z + 1),
                p2.set(x + 1, y + 1, z + 1),
                p3.set(x, y + 1, z + 1),
                removeDuplicateVertices
            );

        }

        // South face (z-)
        if (world.getBlock(worldX, worldY, worldZ - 1) == air.getId()) {
            meshBuilder
                .rect(worldX + 1, worldY, worldZ,
                    worldX, worldY, worldZ,
                    worldX, worldY + 1, worldZ,
                    worldX + 1, worldY + 1, worldZ,
                    0, 0, -1);

            triangleMesh.addTriangle(
                p1.set(x + 1, y, z),
                p2.set(x, y, z),
                p3.set(x, y + 1, z),
                removeDuplicateVertices
            );
            triangleMesh.addTriangle(
                p1.set(x + 1, y, z),
                p2.set(x, y + 1, z),
                p3.set(x + 1, y + 1, z),
                removeDuplicateVertices
            );

        }

        // East face (x+)
        if (world.getBlock(worldX + 1, worldY, worldZ) == air.getId()) {
            meshBuilder
                .rect(worldX + 1, worldY, worldZ + 1,
                    worldX + 1, worldY, worldZ,
                    worldX + 1, worldY + 1, worldZ,
                    worldX + 1, worldY + 1, worldZ + 1,
                    1, 0, 0);

            triangleMesh.addTriangle(
                p1.set(x + 1, y, z + 1),
                p2.set(x + 1, y, z),
                p3.set(x + 1, y + 1, z),
                removeDuplicateVertices
            );
            triangleMesh.addTriangle(
                p1.set(x + 1, y, z + 1),
                p2.set(x + 1, y + 1, z),
                p3.set(x + 1, y + 1, z + 1),
                removeDuplicateVertices
            );
        }

        // West face (x-)
        if (world.getBlock(worldX - 1, worldY, worldZ) == air.getId()) {
            meshBuilder
                .rect(worldX, worldY, worldZ,
                    worldX, worldY, worldZ + 1,
                    worldX, worldY + 1, worldZ + 1,
                    worldX, worldY + 1, worldZ,
                    -1, 0, 0);

            triangleMesh.addTriangle(
                p1.set(x, y, z),
                p2.set(x, y, z + 1),
                p3.set(x, y + 1, z + 1),
                removeDuplicateVertices
            );
            triangleMesh.addTriangle(
                p1.set(x, y, z),
                p2.set(x, y + 1, z + 1),
                p3.set(x, y + 1, z),
                removeDuplicateVertices
            );
        }
    }
}
