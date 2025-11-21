package physics;

import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.utils.Disposable;


/**
 * ARCHITECTURAL ASSESSMENT:
 * 
 * This class represents the collision detection system (System #1 of the three systems).
 * 
 * CURRENT DESIGN:
 * - Uses btCollisionWorld (note: comment mentions future migration to btRigidBody/dynamics world)
 * - Manages collision detection for GameMesh objects
 * - Coupled with rendering through GameMesh
 * 
 * ISSUES:
 * 1. Depends on GameMesh which couples collision and rendering
 * 2. btCollisionObject (current) vs btRigidBody (future) - this is a significant change
 * 3. No separation between different types of collision bodies (static terrain, dynamic entities, triggers)
 * 
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - VIOLATION: This is in the "physics" package but is used by infrastructure.rendering.ObjectRenderer
 * - The dependency arrow is wrong: Infrastructure depends on Physics
 * - Should be: Both depend on abstractions in the application layer
 * 
 * RECOMMENDED REFACTORING:
 * 
 * 1. Rename package to "infrastructure.physics" or "infrastructure.collision"
 *    - This clarifies it's an infrastructure concern like rendering
 * 
 * 2. Create abstraction in application layer:
 *    interface CollisionSystem {
 *        void addCollisionBody(CollisionBody body);
 *        void checkCollisions();
 *        List<CollisionEvent> getCollisionEvents();
 *    }
 * 
 * 3. CollisionHandler implements CollisionSystem:
 *    - Works with pure CollisionBody objects (no rendering data)
 *    - Returns collision events to application layer
 *    - Application layer decides what to do with collisions
 * 
 * 4. Separate static and dynamic collision:
 *    - StaticCollisionBody: For terrain/chunks (never moves, btBvhTriangleMeshShape)
 *    - DynamicCollisionBody: For entities (btRigidBody, affected by physics)
 *    - TriggerCollisionBody: For zones (no physical response, just events)
 * 
 * 5. EntityCollisionUpdater (in presentation layer):
 *    - Reads entity positions from domain layer
 *    - Updates corresponding CollisionBody positions
 *    - Receives collision events from CollisionSystem
 *    - Translates events back to domain operations (damage, pickup, etc.)
 * 
 * This achieves separation: collision system runs independently, synchronized by updater layer
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

    // ARCHITECTURAL ISSUE: Accepts GameMesh which contains both collision and rendering data
    // RECOMMENDATION: Accept CollisionBody instead (pure collision data)
    public void add(GameMesh object){
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
