package application.game_use_cases.GameMesh_Collision;

import domain.entities.Entity;
import domain.entities.EntityType;

public class BulletZombieCollisionInputData {
    Entity zombie;
    Entity bullet;

    public BulletZombieCollisionInputData(Entity a, Entity b){
        if (a.getType() == EntityType.BULLET && b.getType() == EntityType.ZOMBIE){
            this.zombie = b;
            this.bullet = a;
        } else if (a.getType() == EntityType.ZOMBIE && b.getType() == EntityType.BULLET){
            this.zombie = a;
            this.bullet = b;
        } else {
            this.zombie = null;
            this.bullet = null;
        }
    }

    public Entity getBullet() {
        return bullet;
    }

    public Entity getZombie() {
        return zombie;
    }

    public void setBullet(Entity bullet) {
        this.bullet = bullet;
    }

    public void setZombie(Entity zombie) {
        this.zombie = zombie;
    }
}
