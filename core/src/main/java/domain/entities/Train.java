package domain.entities;

import domain.GamePosition;

public class Train extends Entity implements Rideable {
    private final int MAX_FUEL = 100;
    private int fuel;
    private int speed;
    public Train(Integer id, GamePosition position) {
        super(id, EntityType.TRAIN, position, true);
        fuel = 20;
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

    @Override
    public GamePosition getPlayerPosition() {
        return getPosition().add(0f, 2f, 0f);
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
