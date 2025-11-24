package application.use_cases.generate_chunk;

import application.use_cases.generate_entity.train.GenerateTrainInputData;
import application.use_cases.generate_entity.zombie.GenerateZombieInputData;
import application.use_cases.generate_entity.pickup.GeneratePickupInputData;
import application.use_cases.ports.BlockRepository;
import com.badlogic.gdx.math.Vector3;
import domain.Chunk;
import domain.World;
import domain.entities.EntityFactory;
import domain.items.Item;
import domain.items.ItemTypes;
import infrastructure.noise.PerlinNoise;
import java.util.Random;

public class GenerateChunkInteractor implements GenerateChunkInputBoundary {
    private final BlockRepository blockRepository;
    private final EntityFactory entityFactory;
    private final Random random = new Random();

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

                    // Add random zombie
                    if (worldY == height + 3 && (worldX * worldX + worldZ * worldZ) > 100 * 100) {
                        double zombieNoise = PerlinNoise.perlin(
                            worldX * scaleFactor * 10, 100f, worldZ * scaleFactor * 10
                        );
//                        System.out.println(zombieNoise);
                        if (zombieNoise > 0.85){
                            Vector3 pos = new Vector3(worldX, worldY, worldZ);
                            entityFactory.create(new GenerateZombieInputData(pos, chunk));
                        }
                    }

                    //Add random pickups
                    if (worldY == height + 1 && (worldX * worldX + worldZ * worldZ) > 80 * 80) {
                        double pickupNoise = PerlinNoise.perlin(
                            worldX * scaleFactor * 8, 250f, worldZ * scaleFactor * 8
                        );

                        if (pickupNoise > 0.88) {
                            Item item = randomPickupItem();
                            Vector3 pickupPos = new Vector3(worldX, worldY, worldZ);
                            entityFactory.create(new GeneratePickupInputData(item, pickupPos, chunk));
                        }
                    }
                }
            }
        }

//        if (chunk.getChunkZ() > 0 && chunk.getChunkX() <= 0 && chunk.getChunkY() == 0) {
//            for (int x = 0; x < chunkSize; x++) {
//                for (int z = 0; z < chunkSize; z++) {
//                    chunk.setBlock(x, 0, z,  blockRepository.findByName("AIR").orElseThrow());
//                }
//            }
//        }


        // generate rails
        if (chunk.getChunkZ() == 0 && chunk.getChunkY() == 0) {
            for (int x = 0; x < chunkSize; x++) {
                chunk.setBlock(x, 0, 5,  blockRepository.findByName("STONE").orElseThrow());
                chunk.setBlock(x, 0, 10,  blockRepository.findByName("STONE").orElseThrow());
            }
        }

        // train
        if (chunk.getChunkX() == 0 && chunk.getChunkY() == 0 && chunk.getChunkZ() == 0) {
            Vector3 trainPosition = new Vector3(
                chunk.getChunkWorldX() + (chunkSize / 2f),
                1f,
                chunk.getChunkWorldZ() + (chunkSize / 2f)
            );
            GenerateTrainInputData trainInput = new GenerateTrainInputData(trainPosition, chunk);
            entityFactory.create(trainInput);
        }

        if (chunk.getChunkY() == 1) {
            Vector3 pos = chunk.getWorldPosition().add(0, 10, 0);
            entityFactory.create(new GenerateZombieInputData(pos, chunk));
        }

        return new GenerateChunkOutputData(chunk);
    }

    private String getBlockByHeight(int height, int worldY) {
        if (worldY > height) return "AIR";
        if (worldY == height) return "GRASS";
        if (worldY >= height - 3) return "DIRT";
        return "STONE";
    }

    private Item randomPickupItem() {
        // Simple example: coal vs wood log
        int r = random.nextInt(2); // 0 or 1
        switch (r) {
            case 0:
                return ItemTypes.COAL;
            case 1:
            default:
                return ItemTypes.WOOD_LOG;
        }
    }

}
