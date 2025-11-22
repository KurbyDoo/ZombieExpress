package domain.entities;

import com.badlogic.gdx.math.Vector3;

public class Entity {
    protected Integer id;
    protected Vector3 position;
    protected boolean isVisible;

    public Entity(Integer id, Vector3 position, boolean isVisible) {
        this.id = id;
        this.position = position;
        this.isVisible = isVisible;
    }

    public Integer getID() {
        return id;
    }
    public void setID(Integer id) {}

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
