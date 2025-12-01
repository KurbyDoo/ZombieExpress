package application.game_use_cases.populate_chunk;

import application.game_use_cases.generate_entity.pickup.GeneratePickupInputData;
import application.game_use_cases.generate_entity.train.GenerateTrainInputData;
import application.game_use_cases.generate_entity.zombie.GenerateZombieInputData;
import domain.Chunk;
import domain.GamePosition;
import domain.entities.EntityFactory;
import domain.items.Item;
import domain.items.ItemTypes;
import infrastructure.noise.PerlinNoise;
import java.util.Random;

public class PopulateChunkInteractor implements PopulateChunkInputBoundary {
    private final EntityFactory entityFactory;
    private final Random random = new Random();

    public PopulateChunkInteractor(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public void execute(PopulateChunkInputData inputData) {
        Chunk chunk = inputData.getChunk();
        int chunkSize = Chunk.CHUNK_SIZE;
        int maxHeight = chunk.getChunkWorldY() + Chunk.CHUNK_SIZE - 1;
        int minHeight = chunk.getChunkWorldY();

        float scaleFactor = 0.05f;
        // We only spawn things on the surface so skip chunks that are below or in the air
        if (chunk.getMaxBlockHeight() < minHeight || chunk.getMinBlockHeight() > maxHeight) {
            return;
        }

        for (int x = 0; x < chunkSize; x++) {
            for (int z = 0; z < chunkSize; z++) {
                int worldX = x + chunk.getChunkWorldX();
                int worldZ = z + chunk.getChunkWorldZ();

                int surfaceHeight = chunk.getHeight(x, z);
                if (chunk.getChunkWorldY() < surfaceHeight) continue;
                if (isValidSpawn(chunk, worldX, surfaceHeight + 2, worldZ, 100)) {
                    double zombieNoise = PerlinNoise.perlin(
                        worldX * scaleFactor * 10, 100f, worldZ * scaleFactor * 10
                    );

                    if (zombieNoise > 0.85) {
                        GamePosition pos = new GamePosition(worldX, surfaceHeight + 2, worldZ);
                        entityFactory.create(new GenerateZombieInputData(pos));
                    }
                }

                if (isValidSpawn(chunk, worldX, surfaceHeight + 1, worldZ, 80)) {
                    float spawnChance = 0.00075f;
                    if (random.nextFloat() < spawnChance) {
                        Item item = randomPickupItem();
                        GamePosition pickupPos = new GamePosition(worldX, surfaceHeight + 1, worldZ);
                        entityFactory.create(new GeneratePickupInputData(item, pickupPos));
                    }
                }
            }
        }

        // train
        if (chunk.getChunkX() == 0 && chunk.getChunkY() == 0 && chunk.getChunkZ() == 0) {
            GamePosition trainPosition = new GamePosition(
                chunk.getChunkWorldX() + (chunkSize / 2f),
                1f,
                chunk.getChunkWorldZ() + (chunkSize / 2f)
            );
            GenerateTrainInputData trainInput = new GenerateTrainInputData(trainPosition);
            entityFactory.create(trainInput);
        }
    }

    private boolean isValidSpawn(Chunk chunk, int x, int y, int z, int validRadius) {
        int maxHeight = chunk.getChunkWorldY() + Chunk.CHUNK_SIZE - 1;
        int minHeight = chunk.getChunkWorldY();
        return minHeight < y && y < maxHeight && (x * x + z * z) > validRadius * validRadius;
    }


    private Item randomPickupItem() {
        int roll = random.nextInt(100); // 0–99
        if (roll < 26) {                      // 26%
            return ItemTypes.WOOD_LOG;
        } else if (roll < 46) {               // 20% (60-40)
            return ItemTypes.COAL;
        } else if (roll < 56) {               // 10% (70-60)
            return ItemTypes.OIL_BARREL;
        } else if (roll < 66) {               // 10% (80-70)
            return ItemTypes.COMBAT_PISTOL;
        } else if (roll < 76) {               // 10%  (88-80)
            return ItemTypes.TACTICAL_RIFLE;
        } else if (roll < 83) {               // 7%  (93-88)
            return ItemTypes.GOLDEN_PISTOL;
        } else if (roll < 90) {               // 7%  (97-93)
            return ItemTypes.GOLDEN_RIFLE;
        } else if (roll < 95) {               // 5%  (99-97)
            return ItemTypes.RAINBOW_PISTOL;
        } else {                              // 5%  (99-100)
            return ItemTypes.ZOMBIE_OBLITERATOR_RIFLE;
        }
    }
}
