package application.game_features.game_mesh_collision;

import domain.entities.Entity;
import domain.entities.EntityType;

public class BulletZombieCollisionInputData {
    private Entity zombie;
    private Entity bullet;

    public BulletZombieCollisionInputData(Entity first, Entity second) {
        if (first.getType() == EntityType.BULLET && second.getType() == EntityType.ZOMBIE) {
            this.zombie = second;
            this.bullet = first;
        } else if (first.getType() == EntityType.ZOMBIE && second.getType() == EntityType.BULLET) {
            this.zombie = first;
            this.bullet = second;
        } else {
            this.zombie = null;
            this.bullet = null;
        }
    }

    public Entity getBullet() {
        return bullet;
    }

    public void setBullet(Entity bullet) {
        this.bullet = bullet;
    }

    public Entity getZombie() {
        return zombie;
    }

    public void setZombie(Entity zombie) {
        this.zombie = zombie;
    }
}
