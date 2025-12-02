package domain.entities;

import domain.world.GamePosition;

public class PlayerEntity extends Entity {

    private float health = 100f;
    private boolean lifeStatus = true;

    public PlayerEntity(Integer id, GamePosition position) {
        super(id, EntityType.PLAYER, position, false);
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float f) {
        health = f;
    }

    public void lostHealth(float damage) {
        health -= damage;
        if (health <= 0) {
            lifeTerminate();
        }
    }

    public void lifeTerminate() {
        lifeStatus = false;
        System.out.println("player recorded death");
    }

    public boolean getLifeStatus() {
        return lifeStatus;
    }
}
