package application.game_use_cases.GameMesh_Collision;

import domain.GamePosition;
import domain.entities.Bullet;
import domain.entities.Entity;
import domain.entities.Zombie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class BulletZombieCollisionTest {
    private Zombie zombie;
    private Bullet bullet;
    private GamePosition position;
    private BulletZombieCollisionInteractor interactor;



    @BeforeEach
    void setup(){
        position = new GamePosition(0, 0, 0);

        zombie = new Zombie(1, position, true);
        bullet = new Bullet(2, position, new GamePosition(1, 0,0), true);

        interactor = new BulletZombieCollisionInteractor();

    }

    @Test
    void successfulBulletZombieContact(){
        BulletZombieCollisionInputData inputData = new BulletZombieCollisionInputData(zombie, bullet);
        float initialHealth = zombie.getHealth();

        interactor.execute(inputData);

        assertTrue(bullet.isMarkedForRemoval(), "Bullet is markede for removal after colliding.");

        assertEquals(initialHealth - 25f, zombie.getHealth(), "Zombie lost 25 hp due to the collision");

    }

    @Test
    void InputOrderDontMatter(){
        BulletZombieCollisionInputData inputData = new BulletZombieCollisionInputData(bullet, zombie);
        float initialHealth = zombie.getHealth();

        interactor.execute(inputData);

        assertTrue(bullet.isMarkedForRemoval());

        assertEquals(initialHealth - 25f, zombie.getHealth());

    }

}
