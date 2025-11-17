package domain.entities;

import com.badlogic.gdx.math.Vector3;

public class Zombie {
    // Store raw info of the zombies
    private Vector3 position;
    private float speed = 2f;
    private float Health = 100f;
    private boolean visible = false;

    public Zombie(Vector3 position){
        this.position = position;
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getSpeed() { return speed; }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean boolValue) {
        this.visible = boolValue;
    }
}
