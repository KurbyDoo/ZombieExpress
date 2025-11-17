package physics;

import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;

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

    }

    public void add(GameObject object){


        collisionWorld.addCollisionObject(object.body);
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
