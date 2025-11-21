package domain.entities;

import com.badlogic.gdx.math.Vector3;

/**
 * CLEAN ARCHITECTURE - GOOD EXAMPLE:
 * 
 * The Zombie entity is correctly designed as a domain entity:
 * - Contains only business logic and data (position, speed, health)
 * - No dependencies on rendering or collision systems
 * - No knowledge of LibGDX, Bullet physics, or GLTF models
 * 
 * CURRENT STATE:
 * - The 'rendered' flag is a presentation concern that leaked into domain
 * - This is a minor violation but acceptable as a coordination flag
 * 
 * RECOMMENDATION FOR TWO-SYSTEM ARCHITECTURE:
 * This entity should remain as-is. The consolidation should happen at the presentation layer:
 * 
 * 1. Create RenderableEntity interface in infrastructure layer:
 *    interface RenderableEntity {
 *        Vector3 getPosition();
 *        Scene getScene();  // or RenderMesh
 *    }
 * 
 * 2. Create CollidableEntity interface in physics layer:
 *    interface CollidableEntity {
 *        Vector3 getPosition();
 *        CollisionShape getCollisionShape();
 *    }
 * 
 * 3. Create adapters in presentation layer:
 *    - ZombieRenderAdapter implements RenderableEntity (wraps Zombie + Scene)
 *    - ZombieCollisionAdapter implements CollidableEntity (wraps Zombie + CollisionBody)
 * 
 * 4. Create EntityUpdater in presentation layer:
 *    - When Zombie.position changes (via game logic)
 *    - EntityUpdater updates ZombieRenderAdapter's Scene transform
 *    - EntityUpdater updates ZombieCollisionAdapter's CollisionBody transform
 *    - Zombie itself remains unaware of these details
 * 
 * This achieves the goal:
 * - Two systems (rendering, collision) paired with entity layer
 * - Entities unaware of rendering/collision specifics
 * - Updater layer synchronizes entity state with presentation systems
 */
public class Zombie {
    // Store raw info of the zombies
    private Vector3 position;
    private float speed = 2f;
    private float Health = 100f;
    private boolean rendered = false;

    public Zombie(Vector3 position){
        this.position = position;
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getSpeed() { return speed; }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }
}
