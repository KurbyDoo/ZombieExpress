package physics;

import application.game_use_cases.GameMesh_Collision.BulletZombieCollisionInputData;
import application.game_use_cases.GameMesh_Collision.BulletZombieCollisionInteractor;
import application.game_use_cases.GameMesh_Collision.PlayerZombieCollisionInputData;
import application.game_use_cases.GameMesh_Collision.PlayerZombieCollisionInteractor;
import domain.entities.Entity;
import domain.entities.EntityType;
import domain.repositories.EntityStorage;

public class EntityContactFacade {
    EntityStorage storage; // interface

    BulletZombieCollisionInputData bulletZombieCollisionInputData;
    PlayerZombieCollisionInputData playerZombieCollisionInputData;
    public EntityContactFacade(EntityStorage storage){
        this.storage = storage;
    }

    public void resolveCollision(int userId0, int userId1){
        Entity a = storage.getEntityByID(userId0);
        Entity b = storage.getEntityByID(userId1);

        if (a == null || b == null){
            return;
        }

        if ((a.getType() == EntityType.ZOMBIE && b.getType() == EntityType.BULLET) ||
            (b.getType() == EntityType.ZOMBIE && a.getType() == EntityType.BULLET)){

            bulletZombieCollisionInputData = new BulletZombieCollisionInputData(a, b);
            new BulletZombieCollisionInteractor().execute(bulletZombieCollisionInputData);

        }else if ((a.getType() == EntityType.PLAYER && b.getType() == EntityType.ZOMBIE) ||
            (b.getType() == EntityType.PLAYER && a.getType() == EntityType.ZOMBIE)){

            playerZombieCollisionInputData = new PlayerZombieCollisionInputData(a, b);
            new PlayerZombieCollisionInteractor().execute(playerZombieCollisionInputData);
        }

    }

}
