package application.game_use_cases.update_world;

import application.game_use_cases.generate_chunk.GenerateChunkInputBoundary;
import application.game_use_cases.generate_chunk.GenerateChunkInputData;
import application.game_use_cases.generate_chunk.GenerateChunkOutputData;
import application.game_use_cases.render_radius.RenderRadiusManagerInputBoundary;
import application.game_use_cases.render_radius.RenderRadiusManagerInputData;
import application.game_use_cases.render_radius.RenderRadiusOutputData;
import application.game_use_cases.populate_chunk.PopulateChunkInputBoundary;
import application.game_use_cases.populate_chunk.PopulateChunkInputData;
import domain.Chunk;
import domain.GamePosition;
import domain.World;
import domain.player.Player;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UpdateWorldInteractor implements UpdateWorldInputBoundary {
    private final RenderRadiusManagerInputBoundary renderRadiusManager;
    private final GenerateChunkInputBoundary chunkGenerator;
    private final PopulateChunkInputBoundary chunkPopulator;
    private final World world;
    private final Player player;

    public UpdateWorldInteractor(
        RenderRadiusManagerInputBoundary renderRadiusManager,
        GenerateChunkInputBoundary chunkGenerator,
        PopulateChunkInputBoundary chunkPopulator,
        World world,
        Player player
    ) {
        this.renderRadiusManager = renderRadiusManager;
        this.chunkGenerator = chunkGenerator;
        this.chunkPopulator = chunkPopulator;
        this.world = world;
        this.player = player;
    }

    @Override
    public UpdateWorldOutputData execute(UpdateWorldInputData inputData) {
        GamePosition playerPosition = player.getPosition();

        RenderRadiusOutputData radiusData = renderRadiusManager.execute(
            new RenderRadiusManagerInputData(playerPosition, inputData.getRenderRadius())
        );


        List<Integer> activeEntities = world.getEntitiesInChunks(radiusData.getChunksToUpdate());

        for (GamePosition pos : radiusData.getChunksToGenerate()) {
            GenerateChunkOutputData outputData = chunkGenerator.execute(
                new GenerateChunkInputData(pos, world.getWorldDepthChunks() * Chunk.CHUNK_SIZE)
            );
            world.addChunk(pos, outputData.getChunk());
            chunkPopulator.execute(new PopulateChunkInputData(outputData.getChunk()));
        }

        Map<GamePosition, Chunk> toLoad = new HashMap<>();
        for (GamePosition pos : radiusData.getChunksToLoad()) {
            toLoad.put(pos, world.getChunk(pos));
        }

        Map<GamePosition, Chunk> toUnload = new HashMap<>();
        for (GamePosition pos : radiusData.getChunksToUnload()) {
            toUnload.put(pos, world.getChunk(pos));
        }

        return new UpdateWorldOutputData(toLoad, toUnload, activeEntities);
    }
}
