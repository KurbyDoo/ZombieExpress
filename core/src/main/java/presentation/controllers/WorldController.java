package presentation.controllers;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import domain.entities.World;
import domain.entities.Player;
import infrastructure.rendering.ObjectRenderer;
import infrastructure.rendering.ModelGeneratorFacade;
import infrastructure.rendering.ChunkRenderer;
import infrastructure.rendering.BlockMaterialRepository;

import application.use_cases.ChunkRadius.ChunkRadiusManagerInputBoundary;
import application.use_cases.ChunkRadius.ChunkRadiusManagerInputData;
import application.use_cases.ChunkRadius.ChunkRadiusManagerInteractor;
import application.use_cases.chunk_generation.ChunkGenerationInputBoundary;
import application.use_cases.chunk_generation.ChunkGenerationInteractor;
import application.use_cases.chunk_mesh_generation.ChunkMeshGenerationInputBoundary;
import application.use_cases.ports.BlockRepository;


/**
 * Manages all world-related systems, including data, generation, and rendering logic.
 * This acts as a service layer, delegating to use cases and infrastructure components.
 */
public class WorldController implements Disposable {

    private final World world;
    private final Player player;
    private final int RENDER_RADIUS = 6;

    // Use Case Implementations
    private final ChunkGenerationInputBoundary chunkGenerator;
    private final ChunkMeshGenerationInputBoundary chunkMeshGenerator;
    private final ChunkRadiusManagerInputBoundary chunkRadiusManager;

    // Infrastructure/Output Components
    private final ModelGeneratorFacade meshGeneratorFacade;
    private final ChunkRenderer chunkRenderer;

    /**
     * Creates and wires together all world components using provided dependencies.
     */
    public WorldController(
        ObjectRenderer objectRenderer,
        World world,
        Player player,
        BlockRepository blockRepository,
        BlockMaterialRepository materialRepository) {

        this.world = world;
        this.player = player;

        // 1. Initialize Generators (Use Cases)
        this.chunkGenerator = new ChunkGenerationInteractor(blockRepository);
        this.meshGeneratorFacade = new ModelGeneratorFacade(world, blockRepository, materialRepository);
        this.chunkMeshGenerator = this.meshGeneratorFacade; // Facade implements the boundary

        // 2. Initialize Renderer (Output Boundary)
        // Note: ChunkRenderer is Disposable
        this.chunkRenderer = new ChunkRenderer(objectRenderer, this.world);

        // 3. Initialize Manager (Interactor)
        this.chunkRadiusManager = new ChunkRadiusManagerInteractor();
    }

    /**
     * Updates the world state based on the player's current position, triggering
     * chunk generation, meshing, and de-rendering.
     */
    public void update() {
        Vector3 playerPosition = player.getPosition();

        // 1. Assemble the Input Data package
        ChunkRadiusManagerInputData inputData = new ChunkRadiusManagerInputData(
            playerPosition,
            this.world,
            this.chunkGenerator,
            this.chunkMeshGenerator,
            this.chunkRenderer,
            this.RENDER_RADIUS
        );

        // 2. Execute the management logic
        this.chunkRadiusManager.execute(inputData);
    }

    @Override
    public void dispose() {
        // Ensure the renderer is disposed to clean up all chunk models/shapes
        chunkRenderer.dispose();
    }
}
