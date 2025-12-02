package application.game_features.game_mesh_collision;

import domain.entities.PlayerEntity;
import domain.entities.Zombie;
import domain.world.GamePosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerZombieCollisionTest {

    private GamePosition position;
    private PlayerZombieCollisionInteractor interactor;
    private PlayerEntity playerEntity;
    private Zombie zombie;

    @BeforeEach
    void setup(){
        position = new GamePosition(0,0,0);
        interactor = new PlayerZombieCollisionInteractor();
        playerEntity = new PlayerEntity(1, position);
        zombie = new Zombie(2, position, true);
    }

    @Test
    void testPlayerZombieCollision_ShouldDamagePlayer() {
        playerEntity.setHealth(100f); // make sure playerEntity is max health

        PlayerZombieCollisionInputData inputData = new PlayerZombieCollisionInputData(playerEntity, zombie);

        interactor.execute(inputData);

        // Player should lose 30 health (100 - 30 = 70)
        assertEquals(70f, playerEntity.getHealth(), "Player should lose 30 health after collision.");
        assertTrue(playerEntity == inputData.getPlayerEntity());
        assertTrue(zombie == inputData.getZombie());
    }


    @Test
    void testPlayerZombieCollision_ShouldDamagePlayer_inverseLoadOrder() {
        playerEntity.setHealth(100f); // make sure playerEntity is max health

        PlayerZombieCollisionInputData inputData = new PlayerZombieCollisionInputData(zombie, playerEntity);

        interactor.execute(inputData);

        // Player should lose 30 health (100 - 30 = 70)
        assertEquals(70f, playerEntity.getHealth());
        assertTrue(playerEntity == inputData.getPlayerEntity());
        assertTrue(zombie == inputData.getZombie());
    }


    @Test
    void testPlayerZombieCollision_FatalDamageTerminatesLife() {

        // Set player to low health so next hit kills them
        playerEntity.setHealth(20f);

        PlayerZombieCollisionInputData inputData = new PlayerZombieCollisionInputData(playerEntity, zombie);

        interactor.execute(inputData); // Deals 30 damage

        // Health should be -10
        assertEquals(-10f, playerEntity.getHealth());
        // Life status should be false
        assertFalse(playerEntity.getLifeStatus(), "Player lifeStatus should be false if health drops below 0.");
    }


}
