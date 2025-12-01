package domain.entities;

import domain.GamePosition;

public class Entity {
    protected Integer id;
    protected GamePosition position;
    protected GamePosition velocity;
    protected boolean isVisible;
    protected float yaw;
    private final EntityType type;

    protected boolean shouldRemove = false;

    public Entity(Integer id, EntityType type, GamePosition position, boolean isVisible) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.isVisible = isVisible;
        this.velocity = new GamePosition(0, 0, 0);
    }

    public void markForRemoval() {
        this.shouldRemove = true;
    }

    public boolean isMarkedForRemoval() {
        return shouldRemove;
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {}

    public EntityType getType() {
        return type;
    }

    public GamePosition getPosition() {
        return position;
    }

    public void setPosition(GamePosition position) {
        this.position = position;
    }

    public GamePosition getVelocity() {
        return velocity;
    }

    public void setVelocity(GamePosition velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(float x, float y, float z) {
        this.velocity.set(x, y, z);
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean boolValue) {
        this.isVisible = boolValue;
    }
}
