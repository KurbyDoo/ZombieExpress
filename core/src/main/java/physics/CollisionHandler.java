/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Physics)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
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
    private final short GROUND_FLAG = 1 << 8;
    private final short OBJECT_FLAG = 1 << 9;
    private final short ALL_FLAG = -1;

    private final btCollisionConfiguration config;
    private final btDispatcher dispatch;
    private final btBroadphaseInterface broadphase;
    private final btConstraintSolver constraintSolver;

    private final btDiscreteDynamicsWorld dynamicsWorld;
    private final ObjectContactListener listener;

    public CollisionHandler() {
        config = new btDefaultCollisionConfiguration();
        dispatch = new btCollisionDispatcher(config);
        broadphase = new btDbvtBroadphase();
        constraintSolver =  new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatch, broadphase, constraintSolver, config);
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));

        listener = new ObjectContactListener();

    }

    public void stepSimulation(float deltaTime) {
        dynamicsWorld.stepSimulation(deltaTime, 5, 1f/60f);

    }

    public void add(GameMesh object) {
        if (object != null){
            object.getBody().setCollisionFlags(object.getBody().getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
            object.getBody().setUserValue(object.id);

            if (object.getIsStatic()) {
                dynamicsWorld.addRigidBody(object.getBody(), GROUND_FLAG, ALL_FLAG);
            } else{
                dynamicsWorld.addRigidBody(object.getBody(), OBJECT_FLAG, ALL_FLAG);
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
