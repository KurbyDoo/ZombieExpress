package domain.entities;

import domain.GamePosition;

public class Zombie extends Entity {
    // Store raw info of the zombies
    private float speed = 2f;
    private float health = 100f;

    public Zombie(Integer id, GamePosition position, boolean isVisible) {
        super(id, EntityType.ZOMBIE, position, isVisible);  // pass data to Entity
    }

    public void takeDamage(float amount) {
        this.health -= amount;
        if (this.health <= 0) {
            this.markForRemoval(); // Die
        }
    }

    public float getSpeed() { return speed; }

    public float getHealth(){return health;}

    public void setHealth(float health){this.health = health;}
}
