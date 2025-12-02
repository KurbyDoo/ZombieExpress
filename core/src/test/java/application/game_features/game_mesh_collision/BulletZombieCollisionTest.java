package application.game_features.game_mesh_collision;

import domain.entities.Bullet;
import domain.entities.Zombie;
import domain.world.GamePosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BulletZombieCollisionTest {

    private Zombie zombie;
    private Bullet bullet;
    private BulletZombieCollisionInteractor interactor;
    private  GamePosition position;

    @BeforeEach
    void setup(){
        position = new  GamePosition(0,0,0);
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
        float initialHealth = zombie.getHealth();

        interactor.execute(inputData);

        assertTrue(bullet.isMarkedForRemoval());
        assertEquals(initialHealth - 25f, zombie.getHealth());

    }

    @Test
    void gettersTest(){
        BulletZombieCollisionInputData inputData = new BulletZombieCollisionInputData(zombie, bullet);
        assertSame(zombie, inputData.getZombie(), "Returns the same zombie entity");
        assertSame(bullet, inputData.getBullet(), "Returns the same bullet object");
    }

    @Test
    void returnsZombieTest(){
        BulletZombieCollisionInputData inputData = new BulletZombieCollisionInputData(zombie, bullet);

        Zombie zombie1 = new Zombie(3, position, true);
        Bullet bullet1 = new Bullet(4, position, new GamePosition(0,1,1), true);

        inputData.setBullet(bullet1);
        inputData.setZombie(zombie1);

        assertSame(zombie1, inputData.getZombie(), "Replaced zombie using setZombie()");
        assertSame(bullet1, inputData.getBullet(), "Replaced bullet using setBullet()");
    }

}
