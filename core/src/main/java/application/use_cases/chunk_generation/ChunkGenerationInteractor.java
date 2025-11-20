package application.use_cases.chunk_generation;

import application.use_cases.ports.BlockRepository;
import domain.entities.Chunk;
import domain.entities.World;
import infrastructure.noise.PerlinNoise;

public class ChunkGenerationInteractor implements ChunkGenerationInputBoundary {
    private final BlockRepository blockRepository;

    public ChunkGenerationInteractor(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    @Override
    public void execute(ChunkGenerationInputData inputData) {
        Chunk chunk = inputData.getChunk();
        World world = inputData.getWorld();

        int chunkSize = Chunk.CHUNK_SIZE;
        int worldEndX = world.getWorldDepthChunks() * chunkSize;

        float scaleFactor = 0.05f;
        double valleyScale = (double) 1.0f / (chunkSize * chunkSize * 8);

        for (int x = 0; x < chunkSize; x++) {
            for (int z = 0; z < chunkSize; z++) {
                int worldX = x + chunk.getChunkWorldX();
                int worldZ = z + chunk.getChunkWorldZ();
                double perlinNoise = PerlinNoise.octavePerlin(worldX * scaleFactor, 0, worldZ * scaleFactor, 4, 0.6);
                for (int h = 0; h < chunkSize; h++) {
                    int worldY = h + chunk.getChunkWorldY();
                    // offset so the valley is centered on a chunk
                    double valleyHeight = Math.min(8, (worldZ - 7) * (worldZ - 7) * valleyScale);

                    // Chunks behind the start have a valley
                    if (chunk.getChunkX() < 0) {
                        double valleyHeightZ = Math.min(8, worldX * worldX * valleyScale / 2);
                        valleyHeight = Math.max(valleyHeight, valleyHeightZ);
                    }

                    // Chunks beyond the end have a valley
                    if (chunk.getChunkWorldX() > worldEndX) {
                        double valleyHeightZ = Math.min(8, (worldX - worldEndX) * (worldX - worldEndX) * valleyScale / 2);
                        valleyHeight = Math.max(valleyHeight, valleyHeightZ);
                    }
                    int height = (int)(perlinNoise * valleyHeight * valleyHeight);

                    String type = getBlockByHeight(height, worldY);
                    chunk.setBlock(x, h, z, blockRepository.findByName(type).orElseThrow());
                }
            }
        }

        if (chunk.getChunkZ() > 0 && chunk.getChunkX() <= 0 && chunk.getChunkY() == 0) {
            for (int x = 0; x < chunkSize; x++) {
                for (int z = 0; z < chunkSize; z++) {
                    chunk.setBlock(x, 0, z,  blockRepository.findByName("AIR").orElseThrow());
                }
            }
        }


        // generate rails
        if (chunk.getChunkZ() == 0 && chunk.getChunkY() == 0) {
            for (int x = 0; x < chunkSize; x++) {
                chunk.setBlock(x, 0, 5,  blockRepository.findByName("STONE").orElseThrow());
                chunk.setBlock(x, 0, 10,  blockRepository.findByName("STONE").orElseThrow());
            }
        }
    }

    private String getBlockByHeight(int height, int worldY) {
        if (worldY > height) return "AIR";
        if (worldY == height) return "GRASS";
        if (worldY >= height - 3) return "DIRT";
        return "STONE";
    }
}
