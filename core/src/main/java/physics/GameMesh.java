package physics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

/**
 * ARCHITECTURAL ISSUE: GameMesh couples rendering (ModelInstance) with collision (btCollisionObject)
 * 
 * This class represents the CORE PROBLEM in the current architecture:
 * - Extends ModelInstance (rendering concern)
 * - Contains btCollisionObject (collision concern)
 * - These two concerns should be SEPARATED
 * 
 * PROBLEMS:
 * 1. Single Responsibility Principle violation - handles both rendering and collision
 * 2. Forces every rendered object to have collision (and vice versa)
 * 3. Cannot independently update rendering or collision without affecting the other
 * 4. Difficult to optimize - rendering and physics have different update frequencies
 * 
 * MIGRATION NOTE: Comment says this will migrate from btCollisionObject to btRigidBody
 * This will make the problem worse unless the coupling is addressed first.
 * 
 * RECOMMENDED SOLUTION:
 * 1. Create separate classes:
 *    - RenderMesh: Contains Model, ModelInstance, transform for rendering
 *    - CollisionBody: Contains btCollisionShape, btRigidBody, transform for physics
 * 
 * 2. Create an Entity class that owns BOTH:
 *    - Entity has a RenderMesh (optional - some entities may not be visible)
 *    - Entity has a CollisionBody (optional - some entities may not collide)
 *    - Entity has a logical position/transform (source of truth)
 * 
 * 3. Create an EntityUpdater layer:
 *    - Reads Entity logical position
 *    - Updates RenderMesh transform (if present)
 *    - Updates CollisionBody transform (if present)
 *    - Entity layer remains unaware of rendering/collision specifics
 * 
 * This would allow:
 * - Entities without collision (decorative objects, UI elements)
 * - Entities without rendering (trigger zones, invisible walls)
 * - Independent optimization of rendering and physics
 * - Proper separation of concerns per SOLID principles
 */
public class GameMesh extends ModelInstance {

    public Model model;
    public btCollisionShape shape;

    public final btCollisionObject body;
    
    // ARCHITECTURAL ISSUE: Movement logic in data structure
    // This should be in a movement/physics system, not in the data class
    public boolean moving = true;

    public static int COUNTER;
    public int id;

    public GameMesh(Model model, btCollisionShape shape){
        super(model);
        this.model = model;
        this.shape = shape;

        COUNTER++;
        id += COUNTER;

        body = new btCollisionObject();
        body.setCollisionShape(shape);


    }

    public void dispose () {
        body.dispose();
        shape.dispose();
    }

    /**
     * Might be needed since model is disposed at the very end separately from the body & shape.
     */
    public void modelDispose(){
        model.dispose();
    }
}
