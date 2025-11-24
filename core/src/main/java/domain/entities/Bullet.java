package domain.entities;

import com.badlogic.gdx.math.Vector3;

public class Bullet extends Entity{
    public Bullet(Integer id, Vector3 position, boolean isVisible) {
        super(id, EntityType.BULLET, position, isVisible);
    }
}
