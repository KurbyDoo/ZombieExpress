package domain.entities;

import domain.GamePosition;

public class Bullet extends Entity{

    private GamePosition direction;

    public Bullet(Integer id, GamePosition position, GamePosition direction, boolean isVisible) {
        super(id, EntityType.BULLET, position, isVisible);  // pass data to Entity
        this.direction = direction;
    }

    public GamePosition getDirection() {
        return direction;
    }
}
