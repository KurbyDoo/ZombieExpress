package domain.entities;

import domain.GamePosition;

public class Entity {
    protected Integer id;
    protected GamePosition position;
    protected boolean isVisible;
    private final EntityType type;

    public Entity(Integer id, EntityType type, GamePosition position, boolean isVisible) {
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

    public GamePosition getPosition() {
        return position;
    }

    public void setPosition(GamePosition position) {
        this.position = position;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean boolValue) {
        this.isVisible = boolValue;
    }
}
