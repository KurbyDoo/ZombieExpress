package domain.entities;

import domain.world.GamePosition;

public class Bullet extends Entity {

    private GamePosition direction;

    public Bullet(Integer id, GamePosition position, GamePosition direction, boolean isVisible) {
        super(id, EntityType.BULLET, position, isVisible);  // pass data to Entity
        this.direction = direction;
    }

    @Override
    public void markForRemoval() {
        super.markForRemoval();
    }

    @Override
    public boolean isMarkedForRemoval() {
        return super.isMarkedForRemoval();
    }

    public GamePosition getDirection() {
        return direction;
    }
}
