package physics;

import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;


/**
 * creates an btCollisionWorld that calls for the contactListener when any collisions are detected.
 */
public class CollisionHandler implements Disposable {

    public btCollisionConfiguration config;
    public btDispatcher dispatch;
    public btBroadphaseInterface broadphase;
    public btCollisionWorld collisionWorld;
    public ObjectContactListener listener;

    public CollisionHandler(){
        config = new btDefaultCollisionConfiguration();
        dispatch = new btCollisionDispatcher(config);
        broadphase = new btDbvtBroadphase();
        collisionWorld = new btCollisionWorld(dispatch, broadphase, config);

        listener = new ObjectContactListener();

    }

    public void add(GameObject object){
        if (object != null){
            object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
            object.body.setUserValue(object.id);
            collisionWorld.addCollisionObject(object.body);
        }
    }

    public void checkCollision(){
        collisionWorld.performDiscreteCollisionDetection();
    }

    @Override
    public void dispose(){
        dispatch.dispose();
        config.dispose();
        broadphase.dispose();
        collisionWorld.dispose();

        listener.dispose();
    }

}
