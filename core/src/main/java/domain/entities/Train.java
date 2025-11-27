package domain.entities;

import com.badlogic.gdx.math.Vector3;

public class Train extends Entity {

    private int fuel;
    private final int MAX_FUEL = 100;

    public Train(Integer id, EntityType type, Vector3 position, boolean isVisible) {
        super(id, type, position, isVisible);
        this.fuel = 0;
    }

    public int getCurrentFuel() {
        return fuel;
    }

    public int getMaxFuel() {
        return MAX_FUEL;
    }

    public void addFuel(int amount) {
        this.fuel += amount;
        if (this.fuel > MAX_FUEL) {
            this.fuel = MAX_FUEL;
        }
    }

    public void consumeFuel(int amount) {
        this.fuel -= amount;
    }
}
