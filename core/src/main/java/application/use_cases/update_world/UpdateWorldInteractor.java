package application.use_cases.update_world;

import application.use_cases.generate_chunk.GenerateChunkInputBoundary;
import application.use_cases.generate_chunk.GenerateChunkInputData;
import application.use_cases.generate_chunk.GenerateChunkOutputData;
import application.use_cases.populate_chunk.PopulateChunkInputBoundary;
import application.use_cases.populate_chunk.PopulateChunkInputData;
import application.use_cases.render_radius.RenderRadiusManagerInputBoundary;
import application.use_cases.render_radius.RenderRadiusManagerInputData;
import application.use_cases.render_radius.RenderRadiusOutputData;
import domain.GamePosition;
import domain.World;
import domain.player.Player;

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
            new RenderRadiusManagerInputData(playerPosition, world, inputData.getRenderRadius())
        );

        for (GamePosition pos : radiusData.getChunksToGenerate()) {
            // TODO: remove this world dependency
            GenerateChunkOutputData outputData = chunkGenerator.execute(
                new GenerateChunkInputData(pos, world)
            );
            world.addChunk(pos, outputData.getChunk());
            chunkPopulator.execute(new PopulateChunkInputData(world, outputData.getChunk()));

        }

        return new UpdateWorldOutputData(
            radiusData.getChunksToLoad(),
            radiusData.getChunksToUpdate(),
            radiusData.getChunksToUnload()
        );
    }
}
