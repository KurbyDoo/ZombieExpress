package infrastructure.rendering;

import domain.repositories.BlockRepository;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import domain.Block;
import domain.Chunk;
import domain.World;

public class ChunkMeshGenerator {
    private final Vector3 p1 = new Vector3();
    private final Vector3 p2 = new Vector3();
    private final Vector3 p3 = new Vector3();

    private static final Color GRASS_COLOR = new Color(0.52f * 0.5f, 1.0f, 0.36f * 0.5f, 1.0f);

    private final World world;
    private final BlockRepository blockRepository;
    private final TexturedBlockMaterialRepository blockMaterialRepository;
    private final Block air;

    public ChunkMeshGenerator(World world, BlockRepository blockRepository, TexturedBlockMaterialRepository blockMaterialRepository) {
        this.world = world;
        this.blockRepository = blockRepository;
        this.blockMaterialRepository = blockMaterialRepository;
        air = blockRepository.findByName("AIR").orElseThrow();
    }

    public ChunkMeshData createMesh(Chunk chunk) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        btTriangleMesh triangleMesh = new btTriangleMesh();

        for (Block block : blockRepository.findAll()) {
            if (block.getId() != air.getId()) {
                buildType(world, chunk, triangleMesh, modelBuilder, block);
            }
        }

        if (triangleMesh.getNumTriangles() == 0) {
            triangleMesh.dispose();
            modelBuilder.end().dispose();
            return null;
        } else {
            btBvhTriangleMeshShape bvhTriangle = new btBvhTriangleMeshShape(triangleMesh, true);
            Model completeModel = modelBuilder.end();
            return new ChunkMeshData(completeModel, bvhTriangle, triangleMesh);
        }
    }

    private void buildType(World world, Chunk chunk, btTriangleMesh triangleMesh, ModelBuilder modelBuilder, Block type) {
        Material material = blockMaterialRepository.getMaterial(type);

        long attributes = VertexAttributes.Usage.Position |
            VertexAttributes.Usage.Normal |
            VertexAttributes.Usage.TextureCoordinates |
            VertexAttributes.Usage.ColorUnpacked;
        MeshPartBuilder meshBuilder = modelBuilder.part(
            type.toString(),
            GL20.GL_TRIANGLES,
            attributes,
            material
        );

        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    if (chunk.getBlock(x, y, z) == type.getId()) {
                        buildBlockFaces(world, chunk, triangleMesh, meshBuilder, x, y, z, type);
                    }
                }
            }
        }
    }

    private void buildBlockFaces(World world, Chunk chunk, btTriangleMesh triangleMesh, MeshPartBuilder meshBuilder, int x, int y, int z, Block type) {
        int worldX = x + chunk.getChunkX() * Chunk.CHUNK_SIZE;
        int worldY = y + chunk.getChunkY() * Chunk.CHUNK_SIZE;
        int worldZ = z + chunk.getChunkZ() * Chunk.CHUNK_SIZE;

        boolean removeDuplicateVertices = false;
        meshBuilder.setColor(Color.WHITE);

        // Top face (y+)
        if (world.getBlock(worldX, worldY + 1, worldZ) == air.getId()) {
            meshBuilder.setUVRange(blockMaterialRepository.getTextureRegion(type, TexturedBlockMaterialRepository.BlockFace.TOP));

            if (type.getId() == 1) {
                meshBuilder.setColor(GRASS_COLOR);
            }

            meshBuilder.rect(worldX, worldY + 1, worldZ + 1,
                worldX + 1, worldY + 1, worldZ + 1,
                worldX + 1, worldY + 1, worldZ,
                worldX, worldY + 1, worldZ,
                0, 1, 0);

            triangleMesh.addTriangle(
                p1.set(worldX, worldY + 1, worldZ + 1),
                p2.set(worldX + 1, worldY + 1, worldZ + 1),
                p3.set(worldX + 1, worldY + 1, worldZ),
                removeDuplicateVertices);
            triangleMesh.addTriangle(
                p1.set(worldX, worldY + 1, worldZ + 1),
                p2.set(worldX + 1, worldY + 1, worldZ),
                p3.set(worldX, worldY + 1, worldZ),
                removeDuplicateVertices);
        }

        meshBuilder.setColor(Color.WHITE);

        // Bottom face (y-)
        if (world.getBlock(worldX, worldY - 1, worldZ) == air.getId()) {
            meshBuilder.setUVRange(blockMaterialRepository.getTextureRegion(type, TexturedBlockMaterialRepository.BlockFace.BOTTOM));

            meshBuilder.rect(worldX, worldY, worldZ,
                worldX + 1, worldY, worldZ,
                worldX + 1, worldY, worldZ + 1,
                worldX, worldY, worldZ + 1,
                0, -1, 0);

            triangleMesh.addTriangle(
                p1.set(worldX, worldY, worldZ),
                p2.set(worldX + 1, worldY, worldZ),
                p3.set(worldX + 1, worldY, worldZ + 1),
                removeDuplicateVertices);
            triangleMesh.addTriangle(
                p1.set(worldX, worldY, worldZ),
                p2.set(worldX + 1, worldY, worldZ + 1),
                p3.set(worldX, worldY, worldZ + 1),
                removeDuplicateVertices);
        }

        // North face (z+)
        if (world.getBlock(worldX, worldY, worldZ + 1) == air.getId()) {
            meshBuilder.setUVRange(blockMaterialRepository.getTextureRegion(type, TexturedBlockMaterialRepository.BlockFace.SIDE));

            meshBuilder.rect(worldX, worldY, worldZ + 1,
                worldX + 1, worldY, worldZ + 1,
                worldX + 1, worldY + 1, worldZ + 1,
                worldX, worldY + 1, worldZ + 1,
                0, 0, 1);

            triangleMesh.addTriangle(
                p1.set(worldX, worldY, worldZ + 1),
                p2.set(worldX + 1, worldY, worldZ + 1),
                p3.set(worldX + 1, worldY + 1, worldZ + 1),
                removeDuplicateVertices);
            triangleMesh.addTriangle(
                p1.set(worldX, worldY, worldZ + 1),
                p2.set(worldX + 1, worldY + 1, worldZ + 1),
                p3.set(worldX, worldY + 1, worldZ + 1),
                removeDuplicateVertices);
        }

        // South face (z-)
        if (world.getBlock(worldX, worldY, worldZ - 1) == air.getId()) {
            meshBuilder.setUVRange(blockMaterialRepository.getTextureRegion(type, TexturedBlockMaterialRepository.BlockFace.SIDE));

            meshBuilder.rect(worldX + 1, worldY, worldZ,
                worldX, worldY, worldZ,
                worldX, worldY + 1, worldZ,
                worldX + 1, worldY + 1, worldZ,
                0, 0, -1);

            triangleMesh.addTriangle(
                p1.set(worldX + 1, worldY, worldZ),
                p2.set(worldX, worldY, worldZ),
                p3.set(worldX, worldY + 1, worldZ),
                removeDuplicateVertices);
            triangleMesh.addTriangle(
                p1.set(worldX + 1, worldY, worldZ),
                p2.set(worldX, worldY + 1, worldZ),
                p3.set(worldX + 1, worldY + 1, worldZ),
                removeDuplicateVertices);
        }

        // East face (x+)
        if (world.getBlock(worldX + 1, worldY, worldZ) == air.getId()) {
            meshBuilder.setUVRange(blockMaterialRepository.getTextureRegion(type, TexturedBlockMaterialRepository.BlockFace.SIDE));

            meshBuilder.rect(worldX + 1, worldY, worldZ + 1,
                worldX + 1, worldY, worldZ,
                worldX + 1, worldY + 1, worldZ,
                worldX + 1, worldY + 1, worldZ + 1,
                1, 0, 0);

            triangleMesh.addTriangle(
                p1.set(worldX + 1, worldY, worldZ + 1),
                p2.set(worldX + 1, worldY, worldZ),
                p3.set(worldX + 1, worldY + 1, worldZ),
                removeDuplicateVertices);
            triangleMesh.addTriangle(
                p1.set(worldX + 1, worldY,  worldZ+ 1),
                p2.set(worldX + 1, worldY + 1, worldZ),
                p3.set(worldX + 1, worldY + 1, worldZ + 1),
                removeDuplicateVertices);
        }

        // West face (x-)
        if (world.getBlock(worldX - 1, worldY, worldZ) == air.getId()) {
            meshBuilder.setUVRange(blockMaterialRepository.getTextureRegion(type, TexturedBlockMaterialRepository.BlockFace.SIDE));

            meshBuilder.rect(worldX, worldY, worldZ,
                worldX, worldY, worldZ + 1,
                worldX, worldY + 1, worldZ + 1,
                worldX, worldY + 1, worldZ,
                -1, 0, 0);

            triangleMesh.addTriangle(
                p1.set(worldX, worldY, worldZ),
                p2.set(worldX, worldY, worldZ + 1),
                p3.set(worldX + 1, worldY + 1, worldZ + 1),
                removeDuplicateVertices);
            triangleMesh.addTriangle(
                p1.set(worldX, worldY, worldZ),
                p2.set(worldX, worldY + 1, worldZ + 1),
                p3.set(worldX + 1, worldY + 1, worldZ),
                removeDuplicateVertices);
        }
    }
}
