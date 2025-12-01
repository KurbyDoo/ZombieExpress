package framework.physics;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;

/**
 * USE THIS CLASS TO IMPLEMENT TWO OBJECTS' INTERACTIONS.
 * Anytime the collisionHandler detects two objects have contact, this contactListener will be called.
 */
public class ObjectContactListener extends ContactListener  {

    @Override
    public boolean onContactAdded(int userValue0, int partId0, int index0, int userValue1, int partId1, int index1){
//        System.out.println("Objects collided");
        return true;
    }

}
