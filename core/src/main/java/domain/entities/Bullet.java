package domain.entities;

import com.badlogic.gdx.math.Vector3;
import domain.GamePosition;

public class Bullet extends Entity{
    public Bullet(Integer id, GamePosition position, boolean isVisible) {
        super(id, EntityType.BULLET, position, isVisible);
    }
}
