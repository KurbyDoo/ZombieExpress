package application.game_features.generate_chunk;

import application.game_features.generate_chunk.noise.PerlinNoise;
import application.gateways.BlockRepository;
import domain.Chunk;

public class GenerateChunkInteractor implements GenerateChunkInputBoundary {
    private final BlockRepository blockRepository;

    public GenerateChunkInteractor(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    @Override
    public GenerateChunkOutputData execute(GenerateChunkInputData inputData) {
        Chunk chunk = new Chunk(inputData.getPosition());

        int chunkSize = Chunk.CHUNK_SIZE;
        int worldEndX = inputData.getWorldEndX();

        float scaleFactor = 0.05f;
        double valleyScale = (double) 1.0f / (chunkSize * chunkSize * 8);

        for (int x = 0; x < chunkSize; x++) {
            for (int z = 0; z < chunkSize; z++) {
                int worldX = x + chunk.getChunkWorldX();
                int worldZ = z + chunk.getChunkWorldZ();
                double perlinNoise = PerlinNoise.octavePerlin(worldX * scaleFactor, 0, worldZ * scaleFactor, 4, 0.6);
                int height = getLocalHeight(worldZ, valleyScale, chunk, worldX, worldEndX, perlinNoise);
                chunk.setHeight(x, height, z);
                for (int h = 0; h < chunkSize; h++) {
                    int worldY = h + chunk.getChunkWorldY();
                    String type = getBlockByHeight(height, worldY);
                    chunk.setBlock(x, h, z, blockRepository.findByName(type).orElseThrow());

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

        return new GenerateChunkOutputData(chunk);
    }

    private static int getLocalHeight(int worldZ, double valleyScale, Chunk chunk, int worldX, int worldEndX, double perlinNoise) {
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
        return (int)(perlinNoise * valleyHeight * valleyHeight);
    }

    private String getBlockByHeight(int height, int worldY) {
        if (worldY > height) return "AIR";
        if (worldY == height) return "GRASS";
        if (worldY >= height - 3) return "DIRT";
        return "STONE";
    }
}
