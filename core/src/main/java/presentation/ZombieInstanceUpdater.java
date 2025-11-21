package presentation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import domain.entities.ZombieStorage;
import domain.entities.Zombie;
import infrastructure.rendering.ObjectRenderer;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.List;

/**
 * ARCHITECTURAL ANALYSIS - PARTIAL SOLUTION:
 * 
 * This class represents the BEGINNING of the "updater layer" pattern described in requirements:
 * "An 'updater' layer that sits between the presentation layer and the entity layer
 *  to update corresponding meshes accordingly"
 * 
 * WHAT IT DOES RIGHT:
 * - Sits between domain (ZombieStorage/Zombie) and infrastructure (ObjectRenderer)
 * - Translates entity data into rendering calls
 * - Keeps Zombie entity unaware of Scene/SceneManager details
 * 
 * CURRENT LIMITATIONS:
 * 1. Only handles rendering, not collision - should be "EntitySyncManager" that handles BOTH
 * 2. Zombie-specific - should be generalized to handle any entity type
 * 3. One-time creation only - should handle ongoing position updates
 * 4. Hard-coded model path - should use a model registry or factory
 * 
 * RECOMMENDED REFACTORING TO ACHIEVE TWO-SYSTEM ARCHITECTURE:
 * 
 * Rename and generalize this to "EntityRenderUpdater":
 * 
 * class EntityRenderUpdater {
 *     private Map<Entity, Scene> entityScenes;
 *     private ObjectRenderer renderer;
 *     private ModelAssetLoader assetLoader;
 *     
 *     // Called each frame to sync entity positions with render meshes
 *     public void updateRenderMeshes(List<Entity> entities) {
 *         for (Entity entity : entities) {
 *             Scene scene = entityScenes.get(entity);
 *             if (scene == null) {
 *                 scene = createSceneForEntity(entity);
 *                 entityScenes.put(entity, scene);
 *                 renderer.addToSceneManager(scene);
 *             }
 *             // Update transform based on entity position
 *             scene.modelInstance.transform.setTranslation(entity.getPosition());
 *         }
 *     }
 * }
 * 
 * Create parallel "EntityCollisionUpdater":
 * 
 * class EntityCollisionUpdater {
 *     private Map<Entity, btRigidBody> entityBodies;
 *     private CollisionWorld collisionWorld;
 *     
 *     // Called each frame to sync entity positions with collision bodies
 *     public void updateCollisionBodies(List<Entity> entities) {
 *         for (Entity entity : entities) {
 *             btRigidBody body = entityBodies.get(entity);
 *             if (body == null) {
 *                 body = createBodyForEntity(entity);
 *                 entityBodies.put(entity, body);
 *                 collisionWorld.addRigidBody(body);
 *             }
 *             // Update transform based on entity position
 *             body.setWorldTransform(entity.getPosition());
 *         }
 *     }
 * }
 * 
 * This achieves the requirement:
 * - Two systems (render and collision) paired with entity layer
 * - Entities unaware of how rendering and collision work
 * - Updater layer synchronizes everything
 */
public class ZombieInstanceUpdater {
//    private ZombieStorage zombieStorage;
    private ObjectRenderer objectRenderer;
//    private SceneAsset zombieAsset = new GLTFLoader().load(Gdx.files.internal("models/model.gltf"));
//    private SceneAsset zombieAsset = new GLTFLoader().load(Gdx.files.internal("models/factory.gltf"));
    private SceneAsset zombieAsset = new GLTFLoader().load(Gdx.files.internal("models/graveyard.gltf"));
    private boolean created = false;

    public ZombieInstanceUpdater(ObjectRenderer objectRenderer) {
        this.objectRenderer = objectRenderer;
    }

    public void updateRenderList(ZombieStorage zombieStorage) {
        // Create a zombie instance and add to ObjectRender
        List<Zombie> zombies = zombieStorage.getZombies();
        for(Zombie zombie : zombies){
            if (zombie.isRendered() && !created) {
                // a scene is a model instance
                Scene zombieInstance = new Scene(zombieAsset.scene);
//                zombieInstance.modelInstance.transform.setToTranslation(-16f, 0f, -16f);
                zombieInstance.modelInstance.transform.setToTranslation(-16f, 0f, 32f);
                zombieInstance.modelInstance.transform.rotate(Vector3.Y, 180f);
                objectRenderer.addToSceneManager(zombieInstance);
                created = true;
            }
        }
    }
}
