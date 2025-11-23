package domain.entities;

import com.badlogic.gdx.math.Vector3;

public class Entity {
    protected Integer id;
    protected Vector3 position;
    protected boolean isVisible;
    private final EntityType type;

    public Entity(Integer id, EntityType type, Vector3 position, boolean isVisible) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.isVisible = isVisible;
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {}

    public EntityType getType() {
        return type;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean boolValue) {
        this.isVisible = boolValue;
    }
}
