/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Test
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package application.game_use_cases.shoot;

import application.game_use_cases.generate_entity.bullet.GenerateBulletStrategy;
import application.game_use_cases.generate_entity.pickup.GeneratePickupStrategy;
import application.game_use_cases.generate_entity.train.GenerateTrainStrategy;
import application.game_use_cases.generate_entity.zombie.GenerateZombieStrategy;
import application.game_use_cases.mount_entity.MountEntityInputData;
import application.game_use_cases.mount_entity.MountEntityOutputData;
import data_access.IdToEntityStorage;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShootInteractorTest {
    ShootInteractor interactor;
    World world;
    EntityStorage entityStorage;
    EntityFactory entityFactory;

    @BeforeEach
    void setUp() {
        world = new World();
        entityStorage = new MockEntityStorage();
        entityFactory = new EntityFactory.EntityFactoryBuilder(entityStorage)
            .register(EntityType.ZOMBIE, new GenerateZombieStrategy())
            .register(EntityType.TRAIN, new GenerateTrainStrategy())
            .register(EntityType.BULLET, new GenerateBulletStrategy())
            .register(EntityType.PICKUP, new GeneratePickupStrategy())
            .build();
        interactor = new ShootInteractor(entityFactory);
    }

    @Test
    @DisplayName("Shoot bullet")
    void shootBullet() {
        GamePosition playerPos = new GamePosition(10, 5, 20);
        GamePosition playerDir = new GamePosition(0, 0, 1);

        ShootInputData input = new ShootInputData(playerPos, playerDir);

        ShootOutputData output = interactor.execute(input);

        assertNotNull(output.getEntityId(), "Bullet entity with an ID should be created.");

        // Check spawn position
        GamePosition spawn = output.getBulletSpawnPos();
        assertTrue(spawn.x == 10f, "Spawn X should match player X.");
        assertTrue(spawn.y == 5f - 1.5f, "Spawn Y should be player Y minus 1.5.");
        assertTrue(spawn.z == 20f + 0.5f, "Spawn Z should be player Z plus 0.5.");

        // Check direction (copy of player dir)
        GamePosition dir = output.getBulletDir();
        assertTrue(dir.x == 0f, "Direction X should match input.");
        assertTrue(dir.y == 0f, "Direction Y should match input.");
        assertTrue(dir.z == 1f, "Direction Z should match input.");
    }
}
