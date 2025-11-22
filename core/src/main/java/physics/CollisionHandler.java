package physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Disposable;


/**
 * creates an btCollisionWorld that calls for the contactListener when any collisions are detected.
 */
public class CollisionHandler implements Disposable {
    final static short GROUND_FLAG = 1 << 8;
    final static short OBJECT_FLAG = 1 << 9;
    final static short ALL_FLAG = -1;

    public btCollisionConfiguration config;
    public btDispatcher dispatch;
    public btBroadphaseInterface broadphase;
    public btConstraintSolver constraintSolver;

    public btDiscreteDynamicsWorld dynamicsWorld;
    public ObjectContactListener listener;

    public CollisionHandler(){
        config = new btDefaultCollisionConfiguration();
        dispatch = new btCollisionDispatcher(config);
        broadphase = new btDbvtBroadphase();
        constraintSolver =  new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatch, broadphase, constraintSolver, config);
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));

        listener = new ObjectContactListener();

    }

    public void add(GameMesh object){
        if (object != null){
            object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
            object.body.setUserValue(object.id);

            if(object.shape instanceof btBvhTriangleMeshShape){
                dynamicsWorld.addRigidBody(object.body, GROUND_FLAG, ALL_FLAG);
            } else{
                dynamicsWorld.addRigidBody(object.body, OBJECT_FLAG, GROUND_FLAG); //for non world objects
            }

        }
    }

    public void remove(btCollisionObject body) {
        if (body != null) {
            dynamicsWorld.removeCollisionObject(body);
        }
    }

    public void checkCollision(){
        dynamicsWorld.performDiscreteCollisionDetection();
    }

    @Override
    public void dispose(){
        dispatch.dispose();
        config.dispose();
        broadphase.dispose();

        dynamicsWorld.dispose();
        constraintSolver.dispose();

        listener.dispose();
    }

}
