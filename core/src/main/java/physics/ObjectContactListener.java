package physics;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.utils.Disposable;

/**
 * USE THIS CLASS TO IMPLEMENT TWO OBJECTS' INTERACTIONS.
 * Anytime the collisionHandler detects two objects have contact, this contactListener will be called.
 */
public class ObjectContactListener extends ContactListener{

    @Override
    public boolean onContactAdded(btCollisionObject colObj0, int partId0, int index0, btCollisionObject colObj1, int partId1, int index1){
        return true;
    }

}
