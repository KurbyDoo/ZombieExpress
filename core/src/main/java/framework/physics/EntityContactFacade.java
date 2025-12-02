package framework.physics;

import application.game_features.game_mesh_collision.BulletZombieCollisionInputData;
import application.game_features.game_mesh_collision.BulletZombieCollisionInteractor;
import application.game_features.game_mesh_collision.PlayerZombieCollisionInputData;
import application.game_features.game_mesh_collision.PlayerZombieCollisionInteractor;
import application.gateways.EntityStorage;
import domain.entities.Entity;
import domain.entities.EntityType;

public class EntityContactFacade {
    EntityStorage storage; // interface

    BulletZombieCollisionInputData bulletZombieCollisionInputData;
    PlayerZombieCollisionInputData playerZombieCollisionInputData;

    public EntityContactFacade(EntityStorage storage) {
        this.storage = storage;
    }

    public void resolveCollision(int userId0, int userId1) {
        Entity a = storage.getEntityByID(userId0);
        Entity b = storage.getEntityByID(userId1);

        if (a == null || b == null) {
            return;
        }

        if ((a.getType() == EntityType.ZOMBIE && b.getType() == EntityType.BULLET) ||
            (b.getType() == EntityType.ZOMBIE && a.getType() == EntityType.BULLET)) {

            bulletZombieCollisionInputData = new BulletZombieCollisionInputData(a, b);
            new BulletZombieCollisionInteractor().execute(bulletZombieCollisionInputData);

        } else if ((a.getType() == EntityType.PLAYER && b.getType() == EntityType.ZOMBIE) ||
            (b.getType() == EntityType.PLAYER && a.getType() == EntityType.ZOMBIE)) {

            playerZombieCollisionInputData = new PlayerZombieCollisionInputData(a, b);
            new PlayerZombieCollisionInteractor().execute(playerZombieCollisionInputData);
        }

    }

}
