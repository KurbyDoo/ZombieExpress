package domain.entities;

import domain.GamePosition;

public class Zombie extends Entity {
    // Store raw info of the zombies
    private float speed = 2f;
    private float Health = 100f;

    public Zombie(Integer id, GamePosition position, boolean isVisible) {
        super(id, EntityType.ZOMBIE, position, isVisible);  // pass data to Entity
    }

    public float getSpeed() { return speed; }
}
