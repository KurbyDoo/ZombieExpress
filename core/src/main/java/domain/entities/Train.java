package domain.entities;

import domain.GamePosition;

public class Train extends Entity implements Rideable {
    private int maxFuel;
    private int totalFuel;
    private int speed;
    public Train(Integer id, GamePosition position) {
        super(id, EntityType.TRAIN, position, true);
        maxFuel = 100;
        totalFuel = 20;
        speed = 30;
    }

    public int getMaxFuel() {
        return maxFuel;
    }

    public void setMaxFuel(int maxFuel) {
        this.maxFuel = maxFuel;
    }

    public int getTotalFuel() {
        return totalFuel;
    }

    public void setTotalFuel(int totalFuel) {
        this.totalFuel = totalFuel;
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
