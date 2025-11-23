package presentation.controllers;

import application.use_cases.generate_chunk.GenerateChunkInputData;
import application.use_cases.generate_chunk.GenerateChunkOutputData;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputData;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationOutputData;
import application.use_cases.generate_mesh.GenerateMeshInputData;
import application.use_cases.render_radius.RenderRadiusOutputData;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import data_access.EntityStorage;
import domain.Chunk;
import domain.World;
import domain.entities.Entity;
import domain.entities.EntityFactory;
import domain.player.Player;
import infrastructure.rendering.*;

import application.use_cases.render_radius.RenderRadiusManagerInputBoundary;
import application.use_cases.render_radius.RenderRadiusManagerInputData;
import application.use_cases.render_radius.RenderRadiusManagerInteractor;
import application.use_cases.generate_chunk.GenerateChunkInputBoundary;
import application.use_cases.generate_chunk.GenerateChunkInteractor;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputBoundary;
import application.use_cases.ports.BlockRepository;


/**
 * Manages all world-related systems, including data, generation, and rendering logic.
 * This acts as a service layer, delegating to use cases and infrastructure components.
 */
public class WorldSyncController implements Disposable {

    private final World world;
    private final Player player;
    private final int RENDER_RADIUS; // Removed hardcoded '= 6'

    // Use Case Implementations
    private final GenerateChunkInputBoundary chunkGenerator;
    private final ChunkMeshGenerationInputBoundary chunkMeshGenerator;
    private final RenderRadiusManagerInputBoundary renderRadiusManager;

    // Infrastructure/Output Components
    private final ChunkRenderer chunkRenderer;

    private final EntityStorage entityStorage;

    private final MeshFactory meshFactory;
    private final MeshStorage meshStorage;

    /**
     * Creates and wires together all world components using provided dependencies.
     */
    public WorldSyncController(
        ObjectRenderer objectRenderer,
        World world,
        Player player,
        EntityFactory entityFactory,
        EntityStorage entityStorage,
        MeshFactory meshFactory,
        MeshStorage meshStorage,
        BlockRepository blockRepository,
        BlockMaterialRepository materialRepository,
        int renderRadius
    ) {
        this.world = world;
        this.player = player;
        this.RENDER_RADIUS = renderRadius;

        // 1. Initialize Generators (Use Cases)
        // TODO: Move these to game view
        this.chunkGenerator = new GenerateChunkInteractor(blockRepository, entityFactory);
        ModelGeneratorFacade meshGeneratorFacade = new ModelGeneratorFacade(world, blockRepository, materialRepository);
        this.chunkMeshGenerator = meshGeneratorFacade;

        // 2. Initialize Renderer (Output Boundary)
        this.chunkRenderer = new ChunkRenderer(objectRenderer);

        // 3. Initialize Manager (Interactor)
        this.renderRadiusManager = new RenderRadiusManagerInteractor();

        this.entityStorage = entityStorage;

        this.meshFactory = meshFactory;
        this.meshStorage = meshStorage;
    }

    /**
     * Updates the world state based on the player's current position, triggering
     * chunk generation, meshing, and de-rendering.
     */
    public void update() {
        Vector3 playerPosition = player.getPosition();

        // Find chunks to update
        RenderRadiusOutputData radiusData = renderRadiusManager.execute(
            new RenderRadiusManagerInputData(playerPosition, world, RENDER_RADIUS)
        );

        // Generate chunks
        for (Vector3 pos : radiusData.getChunksToGenerate()) {
            GenerateChunkOutputData outputData = chunkGenerator.execute(new GenerateChunkInputData(pos, world));
//            Chunk chunk = outputData.getNewChunk();
            world.addChunk(pos, outputData.getNewChunk());
            // add test zombie to all chunks
//            Vector3 pos = chunk.getWorldPosition().add(0, 10, 0);
//            GenerateZombieInputData zombie = new GenerateZombieInputData(pos, chunk);
//            entityFactory.create(zombie);
            chunkRenderer.onChunkCreated(pos);
        }

        // Load Chunks
        for (Vector3 pos : radiusData.getChunksToLoad()) {
            Chunk chunk = world.getChunk(pos);
            ChunkMeshGenerationOutputData meshOutput = chunkMeshGenerator.execute(
                new ChunkMeshGenerationInputData(world, chunk)
            );

            if (meshOutput.getMeshData() != null) {
                chunkRenderer.onChunkMeshReady(pos, meshOutput.getMeshData());
            }

            for (int id : chunk.getEntityIds()) {
                // TODO: Do i really need to have entity storage here?
                // - check if mesh has been generated
                // - if not, generate mesh and add to mesh storage
                if (meshStorage.hasMesh(id)) continue;
                Entity entity = entityStorage.getEntityByID(id);
                meshFactory.createMesh(new GenerateMeshInputData(entity, id));

            }
        }

        // TODO: Update game logic for all entities that are not in chunks about to be unloaded
        for (Vector3 pos : radiusData.getChunksToUpdate()) {


        }

        // Unload Chunks
        for (Vector3 pos : radiusData.getChunksToUnload()) {
            chunkRenderer.onChunkRemoved(pos);
            // TODO: Unload entities
        }
    }

    @Override
    public void dispose() {
        // Ensure the renderer is disposed to clean up all chunk models/shapes
        chunkRenderer.dispose();
    }
}
