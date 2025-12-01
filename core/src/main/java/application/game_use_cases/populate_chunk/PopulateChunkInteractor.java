/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Interactor Pattern: Implements chunk population use case.
 * - Factory Usage: Uses EntityFactory for entity creation.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [CRITICAL VIOLATION] Imports infrastructure.noise.PerlinNoise.
 *   Use case layer should NOT depend on infrastructure layer.
 *   PerlinNoise should be injected via a NoiseGenerator interface.
 *
 * RECOMMENDED FIX:
 *   Inject NoiseGenerator interface via constructor instead of
 *   directly using infrastructure.noise.PerlinNoise.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - populates chunks with entities.
 * - [PASS] LSP: Correctly implements PopulateChunkInputBoundary.
 * - [WARN] DIP: Depends on concrete PerlinNoise instead of abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Magic numbers (0.05f, etc.) should be named constants.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Multiple magic numbers throughout.
 * - [MINOR] Missing class-level Javadoc.
 */
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
                    double pickupNoise = PerlinNoise.perlin(
                        worldX * scaleFactor * 8, 250f, worldZ * scaleFactor * 8
                    );

                    if (pickupNoise > 0.83) {
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
        int r = random.nextInt(3);
        switch (r) {
            case 0:
                return ItemTypes.COAL;
            case 1:
                return ItemTypes.OIL_BARREL;
            default:
                return ItemTypes.WOOD_LOG;
        }
    }
}
