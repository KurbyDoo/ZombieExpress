package presentation.controllers;

import application.use_cases.chunk_generation.ChunkGenerationInputData;
import application.use_cases.ports.BlockRepository;
import domain.entities.Chunk;
import domain.entities.World;
import application.use_cases.chunk_generation.ChunkGenerationInteractor;
import infrastructure.rendering.ChunkLoader;

public class WorldGenerationController {
    private ChunkGenerationInteractor chunkGenerator;
    private World world;
    private ChunkLoader chunkLoader;

    public WorldGenerationController(World world, ChunkLoader chunkLoader, BlockRepository blockRepository) {
        this.world = world;
        this.chunkLoader = chunkLoader;

        this.chunkGenerator = new ChunkGenerationInteractor(blockRepository);
    }

    public void generateInitialWorld(int worldWidth, int worldHeight, int worldDepth) {
        for (int d = -10; d < worldDepth; d++) {
            for (int x = -worldWidth; x <= worldWidth; x++) {
                for (int y = 0; y <= worldHeight; y++) {
                    generateAndLoadChunk(d, y, x);
                }
            }
        }
    }
    public void generateAndLoadChunk(int chunkX, int chunkY, int chunkZ) {
        Chunk newChunk = world.addChunk(chunkX, chunkY, chunkZ);

        ChunkGenerationInputData inputData = new ChunkGenerationInputData(newChunk, world);
        chunkGenerator.execute(inputData);

        chunkLoader.addChunkToLoad(newChunk);
    }
}
