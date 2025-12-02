package application.game_features.game_mesh_collision;

import domain.entities.Entity;
import domain.entities.EntityType;

public class PlayerZombieCollisionInputData {
    Entity zombie;
    Entity playerEntity;

    public PlayerZombieCollisionInputData(Entity a, Entity b) {
        if (a.getType() == EntityType.PLAYER && b.getType() == EntityType.ZOMBIE) {
            this.zombie = b;
            this.playerEntity = a;
        } else if (a.getType() == EntityType.ZOMBIE && b.getType() == EntityType.PLAYER) {
            this.zombie = a;
            this.playerEntity = b;
        } else {
            this.zombie = null;
            this.playerEntity = null;
        }
    }

    public Entity getZombie() {
        return zombie;
    }

    public Entity getPlayerEntity() {
        return playerEntity;
    }

}


