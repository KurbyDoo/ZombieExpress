package physics;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

/**
 * USE THIS CLASS TO IMPLEMENT TWO OBJECTS' INTERACTIONS.
 * Anytime the collisionHandler detects two objects have contact, this contactListener will be called.
 */
public class ObjectContactListener extends ContactListener  {
    public final EntityContactFacade facade;
    public ObjectContactListener(EntityContactFacade facade){
        this.facade = facade;
    }

    @Override
    public boolean onContactAdded(int userValue0, int partId0, int index0, int userValue1, int partId1, int index1){
        facade.resolveCollision(userValue0, userValue1);
        return true;
    }

}
