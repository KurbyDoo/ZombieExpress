/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Test
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package application.game_use_cases.populate_chunk;

import application.game_use_cases.generate_entity.pickup.GeneratePickupStrategy;
import application.game_use_cases.generate_entity.train.GenerateTrainStrategy;
import application.game_use_cases.generate_entity.zombie.GenerateZombieStrategy;
import data_access.MockEntityStorage;
import domain.Chunk;
import domain.GamePosition;
import domain.World;
import domain.entities.EntityFactory;
import domain.entities.EntityType;
import domain.repositories.EntityStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PopulateChunkEntitiesTest {
    World world;
    Chunk chunk;
    EntityStorage entityStorage;
    EntityFactory entityFactory;
    PopulateChunkInteractor interactor;

    @BeforeEach
    void setUp() {
        world = new World();
        entityStorage = new MockEntityStorage();
        entityFactory = new EntityFactory.EntityFactoryBuilder(entityStorage)
                                         .register(EntityType.ZOMBIE, new GenerateZombieStrategy())
                                         .register(EntityType.TRAIN, new GenerateTrainStrategy())
                                         .register(EntityType.PICKUP, new GeneratePickupStrategy())
                                         .build();
        interactor = new PopulateChunkInteractor(entityFactory);
    }

    @Test
    @DisplayName("Empty near spawn")
    void emptyNearSpawn() {
        chunk = new Chunk(new GamePosition(0, 0, 1));
        world.addChunk(new GamePosition(0, 0, 1), chunk);
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                chunk.setHeight(x, 0, z);
            }
        }
        chunk = new Chunk(new GamePosition(1, 0, 0));
        world.addChunk(new GamePosition(1, 0, 0), chunk);
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                chunk.setHeight(x, 0, z);
            }
        }
        interactor.execute(new PopulateChunkInputData(chunk));
        assertEquals(0, chunk.getEntityIds().size(), "No entities should be near spawn");
    }

    @Test
    @DisplayName("Only the train spawns at the origin")
    void trainSpawn() {
        chunk = new Chunk(new GamePosition(0, 0, 0));
        world.addChunk(new GamePosition(0, 0, 0), chunk);
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                chunk.setHeight(x, 0, z);
            }
        }
        interactor.execute(new PopulateChunkInputData(chunk));
        assertEquals(1, entityStorage.getAllIds().size(), "Train new spawn");
    }

    @Test
    @DisplayName("Air chunk should be empty")
    void emptyInTheAir() {
        chunk = new Chunk(new GamePosition(0, 1, 0));
        world.addChunk(new GamePosition(0, 1, 0), chunk);
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                chunk.setHeight(x, 0, z);
            }
        }
        interactor.execute(new PopulateChunkInputData(chunk));
        assertEquals(0, chunk.getEntityIds().size(), "No entities should be in the air");
    }

    @Test
    @DisplayName("Far chunks should have entities")
    void fullFarAway() {
        for (int i = 0; i < 100; i++) {
            chunk = new Chunk(new GamePosition(20 + i, 0, 0));
            world.addChunk(new GamePosition(20 + i, 0, 0), chunk);
            for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    chunk.setHeight(x, 0, z);
                }
            }
            interactor.execute(new PopulateChunkInputData(chunk));
        }
        assertFalse(entityStorage.getAllIds().isEmpty(), "There should be entities away from spawn");
    }
}
