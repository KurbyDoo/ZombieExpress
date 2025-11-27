package presentation.controllers;

import application.use_cases.generate_chunk.GenerateChunkInputData;
import application.use_cases.generate_chunk.GenerateChunkOutputData;
import application.use_cases.render_radius.RenderRadiusOutputData;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import domain.Chunk;
import domain.GamePosition;
import domain.World;
import domain.player.Player;
import infrastructure.rendering.*;

import application.use_cases.render_radius.RenderRadiusManagerInputBoundary;
import application.use_cases.render_radius.RenderRadiusManagerInputData;
import application.use_cases.generate_chunk.GenerateChunkInputBoundary;

import java.util.Set;


/**
 * Manages all world-related systems, including data, generation, and rendering logic.
 * This acts as a service layer, delegating to use cases and infrastructure components.
 */
public class WorldSyncController implements Disposable {
    private final World world;
    private final Player player;
    private final int RENDER_RADIUS; // Removed hardcoded '= 6'

    private final GenerateChunkInputBoundary chunkGenerator;
    private final RenderRadiusManagerInputBoundary renderRadiusManager;

    private final ChunkRenderer chunkRenderer;

    private RenderRadiusOutputData radiusData;

    /**
     * Creates and wires together all world components using provided dependencies.
     */
    public WorldSyncController(
        int renderRadius,
        World world,
        Player player,
        RenderRadiusManagerInputBoundary renderRadiusManager,
        GenerateChunkInputBoundary chunkGenerator,
        ChunkRenderer chunkRenderer
    ) {
        this.RENDER_RADIUS = renderRadius;
        this.world = world;
        this.player = player;
        this.renderRadiusManager = renderRadiusManager;
        this.chunkGenerator = chunkGenerator;
        this.chunkRenderer = chunkRenderer;}

    /**
     * Updates the world state based on the player's current position, triggering
     * chunk generation, meshing, and de-rendering.
     */
    public void loadUpdate() {
        GamePosition playerPosition = player.getPosition();

        // Find chunks to update
        radiusData = renderRadiusManager.execute(
            new RenderRadiusManagerInputData(playerPosition, world, RENDER_RADIUS)
        );

        // Generate chunks
        for (GamePosition pos : radiusData.getChunksToGenerate()) {
            GenerateChunkOutputData outputData = chunkGenerator.execute(new GenerateChunkInputData(pos, world));
            world.addChunk(pos, outputData.getNewChunk());
        }

        // Load Chunks
        for (GamePosition pos : radiusData.getChunksToLoad()) {
            Chunk chunk = world.getChunk(pos);
            chunkRenderer.loadChunk(pos, chunk);
        }
    }

    public Set<GamePosition> getActiveChunkPositions() {
        // Return the list of chunks currently loaded/updating
        return radiusData.getChunksToUpdate();
    }

    public void unloadUpdate() {
        // Unload Chunks
        for (GamePosition pos : radiusData.getChunksToUnload()) {
            Chunk chunk = world.getChunk(pos);
            chunkRenderer.unloadChunk(pos, chunk);
        }
    }

    @Override
    public void dispose() {
        // Ensure the renderer is disposed to clean up all chunk models/shapes
        chunkRenderer.dispose();
    }
}
