package application.use_cases.generate_chunk;

import application.use_cases.generate_entity.zombie.GenerateZombieInputData;
import application.use_cases.ports.BlockRepository;
import com.badlogic.gdx.math.Vector3;
import domain.Chunk;
import domain.World;
import domain.entities.EntityFactory;
import infrastructure.noise.PerlinNoise;

public class GenerateChunkInteractor implements GenerateChunkInputBoundary {
    private final BlockRepository blockRepository;
    private final EntityFactory entityFactory;

    public GenerateChunkInteractor(BlockRepository blockRepository, EntityFactory entityFactory) {
        this.blockRepository = blockRepository;
        this.entityFactory = entityFactory;
    }

    @Override
    public GenerateChunkOutputData execute(GenerateChunkInputData inputData) {
        Chunk chunk = new Chunk(inputData.getPosition());
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

        Vector3 pos = chunk.getWorldPosition().add(0, 10, 0);
        entityFactory.create(new GenerateZombieInputData(pos, chunk));

        return new GenerateChunkOutputData(chunk);
    }

    private String getBlockByHeight(int height, int worldY) {
        if (worldY > height) return "AIR";
        if (worldY == height) return "GRASS";
        if (worldY >= height - 3) return "DIRT";
        return "STONE";
    }
}
