# Implementation Examples

This document provides concrete code examples showing how to implement the recommended two-system architecture.

## Example 1: Separating RenderMesh and CollisionBody

### Current State (Coupled):
```java
// physics/GameMesh.java - BEFORE
public class GameMesh extends ModelInstance {
    public Model model;                    // Rendering concern
    public btCollisionShape shape;         // Collision concern
    public final btCollisionObject body;   // Collision concern
    public boolean moving = true;          // Movement concern
    // All mixed together!
}
```

### Target State (Separated):

```java
// infrastructure/rendering/RenderMesh.java - NEW
public class RenderMesh {
    private Scene scene;  // Could also be ModelInstance initially
    private Vector3 position;
    private String modelPath;
    
    public RenderMesh(String modelPath, Vector3 initialPosition) {
        this.modelPath = modelPath;
        this.position = new Vector3(initialPosition);
        this.scene = loadScene(modelPath);
        updateTransform();
    }
    
    public void setPosition(Vector3 newPosition) {
        this.position.set(newPosition);
        updateTransform();
    }
    
    private void updateTransform() {
        scene.modelInstance.transform.setTranslation(position);
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public void dispose() {
        // Dispose rendering resources
    }
}

// infrastructure/collision/CollisionBody.java - NEW
public class CollisionBody {
    private btRigidBody rigidBody;
    private btCollisionShape shape;
    private Vector3 position;
    
    public CollisionBody(btCollisionShape shape, Vector3 initialPosition, float mass) {
        this.shape = shape;
        this.position = new Vector3(initialPosition);
        
        btVector3 localInertia = new btVector3(0, 0, 0);
        if (mass != 0f) {
            shape.calculateLocalInertia(mass, localInertia);
        }
        
        btDefaultMotionState motionState = new btDefaultMotionState();
        btRigidBody.btRigidBodyConstructionInfo rbInfo = 
            new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, localInertia);
        rigidBody = new btRigidBody(rbInfo);
        
        updateTransform();
    }
    
    public void setPosition(Vector3 newPosition) {
        this.position.set(newPosition);
        updateTransform();
    }
    
    private void updateTransform() {
        btTransform transform = new btTransform();
        transform.setIdentity();
        transform.setOrigin(new btVector3(position.x, position.y, position.z));
        rigidBody.setWorldTransform(transform);
    }
    
    public btRigidBody getRigidBody() {
        return rigidBody;
    }
    
    public void dispose() {
        rigidBody.dispose();
        shape.dispose();
    }
}
```

## Example 2: Entity Adapters

### Rendering Adapter:

```java
// presentation/adapters/ZombieRenderAdapter.java - NEW
public class ZombieRenderAdapter implements RenderableEntity {
    private final Zombie zombie;  // Domain entity
    private RenderMesh renderMesh;
    private boolean initialized = false;
    
    public ZombieRenderAdapter(Zombie zombie) {
        this.zombie = zombie;
    }
    
    @Override
    public void initialize() {
        if (!initialized) {
            // Load zombie model
            renderMesh = new RenderMesh("models/zombie.gltf", zombie.getPosition());
            initialized = true;
        }
    }
    
    @Override
    public void syncWithEntity() {
        if (initialized) {
            // Update render mesh position based on entity position
            renderMesh.setPosition(zombie.getPosition());
        }
    }
    
    @Override
    public Scene getScene() {
        return initialized ? renderMesh.getScene() : null;
    }
    
    @Override
    public boolean isVisible() {
        return zombie.isRendered();  // Could add culling logic here
    }
    
    @Override
    public void dispose() {
        if (renderMesh != null) {
            renderMesh.dispose();
        }
    }
}
```

### Collision Adapter:

```java
// presentation/adapters/ZombieCollisionAdapter.java - NEW
public class ZombieCollisionAdapter implements CollidableEntity {
    private final Zombie zombie;  // Domain entity
    private CollisionBody collisionBody;
    private boolean initialized = false;
    
    public ZombieCollisionAdapter(Zombie zombie) {
        this.zombie = zombie;
    }
    
    @Override
    public void initialize() {
        if (!initialized) {
            // Create capsule collision shape for zombie
            btCapsuleShape shape = new btCapsuleShape(0.5f, 1.8f);
            collisionBody = new CollisionBody(shape, zombie.getPosition(), 75f); // 75kg mass
            initialized = true;
        }
    }
    
    @Override
    public void syncWithEntity() {
        if (initialized) {
            // Update collision body position based on entity position
            collisionBody.setPosition(zombie.getPosition());
        }
    }
    
    @Override
    public btRigidBody getRigidBody() {
        return initialized ? collisionBody.getRigidBody() : null;
    }
    
    @Override
    public CollisionLayer getCollisionLayer() {
        return CollisionLayer.ENEMY;
    }
    
    @Override
    public void dispose() {
        if (collisionBody != null) {
            collisionBody.dispose();
        }
    }
}
```

## Example 3: Unified Rendering System

```java
// infrastructure/rendering/RenderingSystem.java - NEW
public class RenderingSystem {
    private SceneManager sceneManager;
    private PerspectiveCamera camera;
    private Map<RenderableEntity, Scene> registeredEntities;
    
    public RenderingSystem(PerspectiveCamera camera) {
        this.camera = camera;
        this.sceneManager = new SceneManager();
        this.sceneManager.setCamera(camera);
        this.sceneManager.setAmbientLight(1f);
        this.registeredEntities = new HashMap<>();
    }
    
    /**
     * Register an entity for rendering.
     * Can be called with chunks, zombies, players, or any RenderableEntity.
     */
    public void registerEntity(RenderableEntity entity) {
        entity.initialize();
        Scene scene = entity.getScene();
        if (scene != null) {
            sceneManager.addScene(scene);
            registeredEntities.put(entity, scene);
        }
    }
    
    /**
     * Unregister an entity from rendering.
     */
    public void unregisterEntity(RenderableEntity entity) {
        Scene scene = registeredEntities.remove(entity);
        if (scene != null) {
            sceneManager.removeScene(scene);
        }
        entity.dispose();
    }
    
    /**
     * Update all registered entities with their current state.
     * Called by EntityRenderUpdater each frame.
     */
    public void updateEntities(Collection<RenderableEntity> entities) {
        for (RenderableEntity entity : entities) {
            if (entity.isVisible()) {
                entity.syncWithEntity();
            }
        }
    }
    
    /**
     * Render all scenes. SceneManager handles depth sorting and transparency.
     */
    public void render(float deltaTime) {
        sceneManager.update(deltaTime);
        sceneManager.render();
    }
    
    public void dispose() {
        for (RenderableEntity entity : registeredEntities.keySet()) {
            entity.dispose();
        }
        sceneManager.dispose();
    }
}
```

## Example 4: Unified Collision System

```java
// infrastructure/collision/CollisionSystem.java - NEW
public class CollisionSystem {
    private btDynamicsWorld dynamicsWorld;
    private btBroadphaseInterface broadphase;
    private btCollisionDispatcher dispatcher;
    private btConstraintSolver solver;
    private btCollisionConfiguration config;
    private Map<CollidableEntity, btRigidBody> registeredEntities;
    private List<CollisionEvent> collisionEvents;
    
    public CollisionSystem() {
        config = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(config);
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, config);
        dynamicsWorld.setGravity(new btVector3(0, -9.81f, 0));
        
        registeredEntities = new HashMap<>();
        collisionEvents = new ArrayList<>();
    }
    
    /**
     * Register an entity for collision detection.
     * Can be called with chunks, zombies, players, or any CollidableEntity.
     */
    public void registerEntity(CollidableEntity entity) {
        entity.initialize();
        btRigidBody rigidBody = entity.getRigidBody();
        if (rigidBody != null) {
            // Set collision filter based on entity layer
            short group = entity.getCollisionLayer().getGroup();
            short mask = entity.getCollisionLayer().getMask();
            dynamicsWorld.addRigidBody(rigidBody, group, mask);
            registeredEntities.put(entity, rigidBody);
        }
    }
    
    /**
     * Unregister an entity from collision detection.
     */
    public void unregisterEntity(CollidableEntity entity) {
        btRigidBody rigidBody = registeredEntities.remove(entity);
        if (rigidBody != null) {
            dynamicsWorld.removeRigidBody(rigidBody);
        }
        entity.dispose();
    }
    
    /**
     * Update all registered entities with their current state.
     * Called by EntityCollisionUpdater each frame.
     */
    public void updateEntities(Collection<CollidableEntity> entities) {
        for (CollidableEntity entity : entities) {
            entity.syncWithEntity();
        }
    }
    
    /**
     * Step the physics simulation.
     * Returns collision events that occurred.
     */
    public List<CollisionEvent> stepSimulation(float deltaTime) {
        collisionEvents.clear();
        dynamicsWorld.stepSimulation(deltaTime, 5);
        
        // Collect collision events
        int numManifolds = dispatcher.getNumManifolds();
        for (int i = 0; i < numManifolds; i++) {
            btPersistentManifold manifold = dispatcher.getManifoldByIndexInternal(i);
            if (manifold.getNumContacts() > 0) {
                btCollisionObject obj0 = manifold.getBody0();
                btCollisionObject obj1 = manifold.getBody1();
                // Create collision event
                collisionEvents.add(new CollisionEvent(obj0, obj1, manifold));
            }
        }
        
        return collisionEvents;
    }
    
    public void dispose() {
        for (CollidableEntity entity : registeredEntities.keySet()) {
            entity.dispose();
        }
        dynamicsWorld.dispose();
        solver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        config.dispose();
    }
}
```

## Example 5: Entity Synchronization Manager (Updater Layer)

```java
// presentation/updaters/EntitySynchronizationManager.java - NEW
public class EntitySynchronizationManager {
    private final RenderingSystem renderingSystem;
    private final CollisionSystem collisionSystem;
    
    // Maps to track adapters for each entity type
    private Map<Zombie, ZombieRenderAdapter> zombieRenderAdapters;
    private Map<Zombie, ZombieCollisionAdapter> zombieCollisionAdapters;
    private Map<Chunk, ChunkRenderAdapter> chunkRenderAdapters;
    private Map<Chunk, ChunkCollisionAdapter> chunkCollisionAdapters;
    
    public EntitySynchronizationManager(RenderingSystem renderingSystem, 
                                        CollisionSystem collisionSystem) {
        this.renderingSystem = renderingSystem;
        this.collisionSystem = collisionSystem;
        this.zombieRenderAdapters = new HashMap<>();
        this.zombieCollisionAdapters = new HashMap<>();
        this.chunkRenderAdapters = new HashMap<>();
        this.chunkCollisionAdapters = new HashMap<>();
    }
    
    /**
     * Register a zombie entity for both rendering and collision.
     */
    public void registerZombie(Zombie zombie) {
        // Create and register render adapter
        ZombieRenderAdapter renderAdapter = new ZombieRenderAdapter(zombie);
        renderingSystem.registerEntity(renderAdapter);
        zombieRenderAdapters.put(zombie, renderAdapter);
        
        // Create and register collision adapter
        ZombieCollisionAdapter collisionAdapter = new ZombieCollisionAdapter(zombie);
        collisionSystem.registerEntity(collisionAdapter);
        zombieCollisionAdapters.put(zombie, collisionAdapter);
    }
    
    /**
     * Unregister a zombie entity from both systems.
     */
    public void unregisterZombie(Zombie zombie) {
        ZombieRenderAdapter renderAdapter = zombieRenderAdapters.remove(zombie);
        if (renderAdapter != null) {
            renderingSystem.unregisterEntity(renderAdapter);
        }
        
        ZombieCollisionAdapter collisionAdapter = zombieCollisionAdapters.remove(zombie);
        if (collisionAdapter != null) {
            collisionSystem.unregisterEntity(collisionAdapter);
        }
    }
    
    /**
     * Register a chunk entity for both rendering and collision.
     */
    public void registerChunk(Chunk chunk) {
        // Create and register render adapter
        ChunkRenderAdapter renderAdapter = new ChunkRenderAdapter(chunk);
        renderingSystem.registerEntity(renderAdapter);
        chunkRenderAdapters.put(chunk, renderAdapter);
        
        // Create and register collision adapter
        ChunkCollisionAdapter collisionAdapter = new ChunkCollisionAdapter(chunk);
        collisionSystem.registerEntity(collisionAdapter);
        chunkCollisionAdapters.put(chunk, collisionAdapter);
    }
    
    /**
     * Update all entities - synchronizes entity state with rendering and collision systems.
     * Called each frame by GameView.
     */
    public void updateAll() {
        // Update rendering system with current entity states
        renderingSystem.updateEntities(zombieRenderAdapters.values());
        renderingSystem.updateEntities(chunkRenderAdapters.values());
        
        // Update collision system with current entity states
        collisionSystem.updateEntities(zombieCollisionAdapters.values());
        collisionSystem.updateEntities(chunkCollisionAdapters.values());
    }
    
    /**
     * Handle collision events - translate physics events back to game logic.
     */
    public void processCollisions(List<CollisionEvent> collisionEvents) {
        for (CollisionEvent event : collisionEvents) {
            // Determine what collided and trigger appropriate game logic
            // Example: zombie collided with player → damage player
            // This is where physics results feed back into domain logic
        }
    }
    
    public void dispose() {
        // Cleanup all adapters
        for (ZombieRenderAdapter adapter : zombieRenderAdapters.values()) {
            renderingSystem.unregisterEntity(adapter);
        }
        for (ZombieCollisionAdapter adapter : zombieCollisionAdapters.values()) {
            collisionSystem.unregisterEntity(adapter);
        }
        // Same for chunks...
    }
}
```

## Example 6: Updated GameView (Integration)

```java
// presentation/view/GameView.java - AFTER refactoring
public class GameView implements Viewable {
    // Two unified systems
    private RenderingSystem renderingSystem;
    private CollisionSystem collisionSystem;
    
    // Updater layer
    private EntitySynchronizationManager entitySync;
    
    // Domain entities
    private Player player;
    private World world;
    private ZombieStorage zombieStorage;
    
    // Controllers and use cases (unchanged)
    private EntityController entityController;
    
    @Override
    public void createView() {
        // Initialize camera
        ViewCamera camera = new ViewCamera();
        
        // Create the two unified systems
        renderingSystem = new RenderingSystem(camera);
        collisionSystem = new CollisionSystem();
        
        // Create the updater layer that bridges entity and infrastructure
        entitySync = new EntitySynchronizationManager(renderingSystem, collisionSystem);
        
        // Create domain entities (unchanged)
        player = new Player(new Vector3(0, 16f, 0));
        world = new World();
        zombieStorage = new ZombieStorage();
        
        // Create use cases (unchanged)
        EntityGenerationInteractor entityGenInteractor = 
            new EntityGenerationInteractor(zombieStorage);
        RenderZombieInteractor renderZombieInteractor = 
            new RenderZombieInteractor(zombieStorage);
        
        // Create controllers (unchanged)
        entityController = new EntityController(
            entityGenInteractor, 
            renderZombieInteractor, 
            zombieStorage,
            entitySync  // Pass the synchronization manager instead of specific updater
        );
        
        // Generate initial world
        generateWorld();
        
        // Generate entities
        entityController.generateZombie();
    }
    
    private void generateWorld() {
        // Generate chunks (domain entities)
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                Chunk chunk = world.addChunk(x, 0, z);
                // Populate chunk with blocks...
                
                // Register chunk with both systems via updater layer
                entitySync.registerChunk(chunk);
            }
        }
    }
    
    @Override
    public void renderView() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        
        // GAME LOGIC UPDATES (entity layer)
        updateGameLogic(deltaTime);
        
        // SYNCHRONIZATION (updater layer)
        entitySync.updateAll();
        
        // PHYSICS SIMULATION (collision system)
        List<CollisionEvent> collisions = collisionSystem.stepSimulation(deltaTime);
        entitySync.processCollisions(collisions);
        
        // RENDERING (rendering system)
        renderingSystem.render(deltaTime);
    }
    
    private void updateGameLogic(float deltaTime) {
        // Update entities (pure domain logic)
        // Example: AI updates zombie positions
        // Example: Player input updates player position
        // These changes will be synced by entitySync.updateAll()
    }
    
    @Override
    public void disposeView() {
        entitySync.dispose();
        renderingSystem.dispose();
        collisionSystem.dispose();
    }
}
```

## Example 7: Interfaces

```java
// infrastructure/rendering/RenderableEntity.java - NEW
public interface RenderableEntity {
    /**
     * Initialize rendering resources (load models, create scenes).
     */
    void initialize();
    
    /**
     * Synchronize render mesh with entity state.
     * Called each frame to update position, rotation, animation, etc.
     */
    void syncWithEntity();
    
    /**
     * Get the Scene object for rendering.
     */
    Scene getScene();
    
    /**
     * Check if entity should be rendered (visibility, culling).
     */
    boolean isVisible();
    
    /**
     * Dispose rendering resources.
     */
    void dispose();
}

// infrastructure/collision/CollidableEntity.java - NEW
public interface CollidableEntity {
    /**
     * Initialize collision resources (create shapes, bodies).
     */
    void initialize();
    
    /**
     * Synchronize collision body with entity state.
     * Called each frame to update position, velocity, etc.
     */
    void syncWithEntity();
    
    /**
     * Get the rigid body for physics simulation.
     */
    btRigidBody getRigidBody();
    
    /**
     * Get the collision layer for filtering.
     */
    CollisionLayer getCollisionLayer();
    
    /**
     * Dispose collision resources.
     */
    void dispose();
}

// infrastructure/collision/CollisionLayer.java - NEW
public enum CollisionLayer {
    TERRAIN((short)0x0001, (short)0xFFFF),    // Collides with everything
    PLAYER((short)0x0002, (short)0xFFFD),     // Collides with all except other players
    ENEMY((short)0x0004, (short)0x0003),      // Collides with terrain and player
    PROJECTILE((short)0x0008, (short)0x0005); // Collides with terrain and enemy
    
    private final short group;
    private final short mask;
    
    CollisionLayer(short group, short mask) {
        this.group = group;
        this.mask = mask;
    }
    
    public short getGroup() { return group; }
    public short getMask() { return mask; }
}
```

## Migration Path: Backward Compatibility

During migration, you can keep both old and new systems running:

```java
// GameView.java - DURING MIGRATION
public class GameView implements Viewable {
    // OLD SYSTEM (to be deprecated)
    private ObjectRenderer oldObjectRenderer;
    private CollisionHandler oldCollisionHandler;
    
    // NEW SYSTEM
    private RenderingSystem newRenderingSystem;
    private CollisionSystem newCollisionSystem;
    private EntitySynchronizationManager entitySync;
    
    // Feature flag to switch between systems
    private boolean useNewSystem = false;
    
    @Override
    public void renderView() {
        if (useNewSystem) {
            // Use new two-system architecture
            entitySync.updateAll();
            List<CollisionEvent> collisions = newCollisionSystem.stepSimulation(deltaTime);
            newRenderingSystem.render(deltaTime);
        } else {
            // Use old three-system architecture
            oldObjectRenderer.render(deltaTime);
        }
    }
}
```

This allows:
1. Gradual migration one entity type at a time
2. A/B testing to verify new system works correctly
3. Easy rollback if issues are discovered
4. Continuous integration during development

## Summary

These examples demonstrate:
- ✅ Separation of RenderMesh and CollisionBody
- ✅ Entity adapters that bridge domain and infrastructure
- ✅ Unified RenderingSystem using SceneManager exclusively
- ✅ Unified CollisionSystem using btDynamicsWorld
- ✅ EntitySynchronizationManager as the updater layer
- ✅ Clean interfaces for extensibility
- ✅ Migration path that maintains backward compatibility

The key insight is that **entities remain pure domain objects**, while **adapters in the presentation layer** translate them into infrastructure-specific representations (Scene for rendering, btRigidBody for collision). The **updater layer coordinates everything** without coupling the systems together.
