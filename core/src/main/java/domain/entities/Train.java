package domain.entities;

import com.badlogic.gdx.math.Vector3;

public class Train extends Entity {

    private int fuel;

    public Train(Integer id, EntityType type, Vector3 position, boolean isVisible) {
        super(id, type, position, isVisible);
        this.fuel = 0;
    }

    public int getCurrentFuel() {
        return fuel;
    }

    public int getMaxFuel() {
        return 100;
    }

    public void addFuel(int amount) {
        this.fuel += amount;
        if (this.fuel > 100) {
            this.fuel = 100;
        }
    }

    public void consumeFuel(int amount) {
        this.fuel -= amount;
    }
}
