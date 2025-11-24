package application.use_cases.chunk_mesh_generation;

import com.badlogic.gdx.graphics.g3d.Model;
import application.use_cases.ports.BlockRepository;
import domain.Block;
import domain.Chunk;
import domain.World;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import infrastructure.rendering.ChunkMeshData;
import infrastructure.rendering.BlockMaterialRepository;
import physics.GameMesh;


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

        // If the mesh is empty, clean up and return empty null
        ChunkMeshData meshData;
        if (triangleMesh.getNumTriangles() == 0) {
            triangleMesh.dispose();
            modelBuilder.end().dispose();
            meshData = null;
        } else {
            btBvhTriangleMeshShape bvhTriangle = new btBvhTriangleMeshShape(triangleMesh, true);
            Model completeModel = modelBuilder.end();
            meshData = new ChunkMeshData(completeModel, bvhTriangle, triangleMesh);
        }

        return new ChunkMeshGenerationOutputData(meshData);
    }


    private void buildType(World world, Chunk chunk, btTriangleMesh triangleMesh, ModelBuilder modelBuilder, Block type) {
        Material material = blockMaterialRepository.getMaterial(type);
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
        // Top face (y+)
        if (world.getBlock(worldX, worldY + 1, worldZ) == air.getId()) {
            meshBuilder
                .rect(worldX, worldY + 1, worldZ + 1,
                    worldX + 1, worldY + 1, worldZ + 1,
                    worldX + 1, worldY + 1, worldZ,
                    worldX, worldY + 1, worldZ,
                    0, 1, 0);

            // Generate individual TriangleMeshes.
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

        // Bottom face (y-)
        if (world.getBlock(worldX, worldY - 1, worldZ) == air.getId()) {
            meshBuilder
                .rect(worldX, worldY, worldZ,
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
            meshBuilder
                .rect(worldX, worldY, worldZ + 1,
                    worldX + 1, worldY, worldZ + 1,
                    worldX + 1, worldY + 1, worldZ + 1,
                    worldX, worldY + 1, worldZ + 1,
                    0, 0, 1);

                triangleMesh.addTriangle(
                    p1.set(worldX, worldY, worldZ + 1),
                    p2.set(worldX + 1, worldY, worldZ + 1),
                    p3.set(worldX + 1, worldY + 1, worldZ + 1),
                    removeDuplicateVertices
                );
                triangleMesh.addTriangle(
                    p1.set(worldX, worldY, worldZ + 1),
                    p2.set(worldX + 1, worldY + 1, worldZ + 1),
                    p3.set(worldX, worldY + 1, worldZ + 1),
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
                    p1.set(worldX + 1, worldY, worldZ),
                    p2.set(worldX, worldY, worldZ),
                    p3.set(worldX, worldY + 1, worldZ),
                    removeDuplicateVertices
                );
                triangleMesh.addTriangle(
                    p1.set(worldX + 1, worldY, worldZ),
                    p2.set(worldX, worldY + 1, worldZ),
                    p3.set(worldX + 1, worldY + 1, worldZ),
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
                    p1.set(worldX + 1, worldY, worldZ + 1),
                    p2.set(worldX + 1, worldY, worldZ),
                    p3.set(worldX + 1, worldY + 1, worldZ),
                    removeDuplicateVertices
                );
                triangleMesh.addTriangle(
                    p1.set(worldX + 1, worldY,  worldZ+ 1),
                    p2.set(worldX + 1, worldY + 1, worldZ),
                    p3.set(worldX + 1, worldY + 1, worldZ + 1),
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
                    p1.set(worldX, worldY, worldZ),
                    p2.set(worldX, worldY, worldZ + 1),
                    p3.set(worldX, worldY + 1, worldZ + 1),
                    removeDuplicateVertices
                );
                triangleMesh.addTriangle(
                    p1.set(worldX, worldY, worldZ),
                    p2.set(worldX, worldY + 1, worldZ + 1),
                    p3.set(worldX, worldY + 1, worldZ),
                    removeDuplicateVertices
                );
        }
    }
}
