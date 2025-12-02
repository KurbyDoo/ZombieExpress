package application.game_use_cases.GameMesh_Collision;

import domain.GamePosition;
import domain.entities.Bullet;
import domain.entities.Zombie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BulletZombieCollisionTest {

    private Zombie zombie;
    private Bullet bullet;
    private BulletZombieCollisionInteractor interactor;
    private GamePosition position;
    @BeforeEach
    void setup(){
        position = new GamePosition(0,0,0);
        interactor = new BulletZombieCollisionInteractor();

        bullet = new Bullet(1, position, new GamePosition(1, 0, 0), true);
        zombie = new Zombie(2, position, true);
    }

    @Test
    void successfulBulletZombieContact(){
        BulletZombieCollisionInputData inputData = new BulletZombieCollisionInputData(bullet, zombie);
        float initialHealth = zombie.getHealth();

        interactor.execute(inputData);

        assertTrue(bullet.isMarkedForRemoval(), "Bullet show be removed after the collision");
        assertEquals(initialHealth - 25f, zombie.getHealth(), "Zombie loses 25hp after contact with bullet");
    }

    @Test
    void loadOrderDoesntMatter(){
        // reverse the load order compared to the previous test
        BulletZombieCollisionInputData inputData = new BulletZombieCollisionInputData(zombie, bullet);
    }

}
