package domain.entities;

import com.badlogic.gdx.math.Vector3;

public class Train extends Entity {
    public Train(Integer id, EntityType type, Vector3 position, boolean isVisible) {
        super(id, type, position, isVisible);
    }
}
