package domain.entities;

import domain.GamePosition;

public class PlayerEntity extends Entity{

    private float health = 300f;
    private boolean lifeStatus = true;
    public PlayerEntity (Integer id, EntityType type, GamePosition position, boolean isVisible){
        super(id, type, position, isVisible);
    }

    public float getHealth(){return health;}

    public void setHealth(float f){heatlh = f;}

    public void lostHealth(float damage){
        health -= damage;
        if (health <= 0){
            lifeTerminate();
        }
    }

    public void lifeTerminate(){lifeStatus = false;}

    public boolean getLifeStatus(){return lifeStatus;}
}
