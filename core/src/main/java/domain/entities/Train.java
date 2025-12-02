package domain.entities;

import domain.GamePosition;

public class Train extends Entity implements Rideable {
    private final float MAX_FUEL = 100;
    private final float MAX_THROTTLE = 2f;
    private float currentThrottle = 0f;

    private float fuel;
    private int speed;
    public Train(Integer id, GamePosition position) {
        super(id, EntityType.TRAIN, position, true);
        fuel = 0;
        speed = 30;
    }

    public void stop() {
        this.speed = 0;
        this.currentThrottle = 0;
    }

    public void setCurrentFuel(float fuel) {
        this.fuel = Math.min(fuel, MAX_FUEL);
    }

    public float getCurrentFuel() {
        return fuel;
    }

    public float getMaxFuel() {
        return MAX_FUEL;
    }

    public void addFuel(float amount) {
        this.fuel = Math.min(this.fuel + amount, MAX_FUEL);
    }

    public void consumeFuel(float amount) {
        this.fuel = Math.max(this.fuel - amount, 0);
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

    public float getRemainingThrottle() {
        return MAX_THROTTLE - currentThrottle;
    }
}
