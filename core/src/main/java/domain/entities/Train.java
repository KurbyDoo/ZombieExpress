package domain.entities;

import domain.GamePosition;

public class Train extends Entity implements Rideable {
    private final int MAX_FUEL = 10000;
    private final float MAX_THROTTLE = 1f;
    private float currentThrottle = 0f; // Range -1 to 1

    private int fuel;
    private int speed;
    public Train(Integer id, GamePosition position) {
        super(id, EntityType.TRAIN, position, true);
        fuel = 2000;
        speed = 30;
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

    public GamePosition getRideOffset() {
        return new GamePosition(2f, 6f, 0f);
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setThrottle(float throttle) {
        this.currentThrottle = throttle;
    }

    public float getThrottle() {
        return currentThrottle;
    }

    public void resetThrottle() {
        this.currentThrottle = 0f;
    }

    public void accelerate() {
        setThrottle(currentThrottle + (MAX_THROTTLE - currentThrottle) * 0.02f);
    }

    public void decelerate() {
        setThrottle(currentThrottle * 0.99f);
    }
}
