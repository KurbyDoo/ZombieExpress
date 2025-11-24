package domain.entities;

import com.badlogic.gdx.math.Vector3;

public class Zombie extends Entity {
    // Store raw info of the zombies
    private float speed = 2f;
    private float Health = 100f;

    public Zombie(Integer id, Vector3 position, boolean isVisible) {
        super(id, EntityType.ZOMBIE, position, isVisible);  // pass data to Entity
    }

    public float getSpeed() { return speed; }
}
