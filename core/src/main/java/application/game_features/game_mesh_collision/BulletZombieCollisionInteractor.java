package application.game_features.game_mesh_collision;

import domain.entities.Bullet;
import domain.entities.Zombie;

public class BulletZombieCollisionInteractor implements BulletZombieCollisionInputBoundary {

    @Override
    public void execute(BulletZombieCollisionInputData inputData) {
        Bullet bullet = (Bullet) inputData.getBullet();
        Zombie zombie = (Zombie) inputData.getZombie();

        bullet.markForRemoval();

        zombie.takeDamage(25f);

        System.out.println("Collision Logic: Bullet hit Zombie ID " + zombie.getID());
    }
}
