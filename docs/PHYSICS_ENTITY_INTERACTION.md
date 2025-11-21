# Physics-Entity Interaction Architecture

## Problem Statement

The current architecture needs to handle **bidirectional data flow** between the physics system and entity layer:

1. **Entities → Physics**: Entity positions/velocities update collision bodies (already addressed in previous research)
2. **Physics → Entities**: Physics simulation results (collisions, repositioning) need to update entity state

This creates a critical architectural question: **How do we maintain Clean Architecture principles when physics needs to update entities?**

## Current State Analysis

### What Happens Now

Currently, the system uses:
- `btCollisionWorld` (collision detection only, no physics simulation)
- `ObjectContactListener` that prints collision events but doesn't update entities
- No mechanism for physics to reposition entities after collision

### The Challenge

When migrating to `btDynamicsWorld` with `btRigidBody`:
- Physics simulation will calculate new positions based on forces, collisions, constraints
- These calculated positions need to flow back to domain entities
- **Problem**: Clean Architecture says domain entities shouldn't depend on infrastructure (physics)
- **Conflict**: But physics needs to update entity positions!

## Architectural Patterns for Physics-Entity Interaction

### Pattern 1: Event-Driven Architecture (RECOMMENDED)

**Principle**: Physics system emits events, application layer interprets and applies them to entities.

```
┌──────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                              │
│                                                               │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐   │
│  │   Player    │     │   Zombie    │     │   Chunk     │   │
│  │             │     │             │     │             │   │
│  │  position   │     │  position   │     │  blocks     │   │
│  │  velocity   │     │  velocity   │     │             │   │
│  └─────────────┘     └─────────────┘     └─────────────┘   │
│         ▲                   ▲                                │
└─────────┼───────────────────┼────────────────────────────────┘
          │                   │
          │ applyPhysicsUpdate()
          │                   │
┌─────────┼───────────────────┼────────────────────────────────┐
│         │  APPLICATION LAYER│                                │
│         │                   │                                │
│  ┌──────┴───────────────────┴──────┐                        │
│  │   PhysicsEventInterpreter       │                        │
│  │                                  │                        │
│  │  onCollision(event) {            │                        │
│  │    if (zombie collides player)   │                        │
│  │      player.takeDamage()         │                        │
│  │  }                               │                        │
│  │                                  │                        │
│  │  onPositionUpdate(event) {       │                        │
│  │    entity.setPosition(newPos)    │                        │
│  │    entity.setVelocity(newVel)    │                        │
│  │  }                               │                        │
│  └──────────────▲───────────────────┘                        │
│                 │                                             │
│                 │ emits events                                │
└─────────────────┼─────────────────────────────────────────────┘
                  │
┌─────────────────┼─────────────────────────────────────────────┐
│  INFRASTRUCTURE │LAYER (Physics)                              │
│                 │                                             │
│  ┌──────────────┴─────────────────┐                          │
│  │    PhysicsSystem                │                          │
│  │                                 │                          │
│  │  stepSimulation() {             │                          │
│  │    btDynamicsWorld.step()       │                          │
│  │                                 │                          │
│  │    // Collect results           │                          │
│  │    for body in bodies {         │                          │
│  │      newPos = body.getPosition()│                          │
│  │      emit PositionUpdateEvent   │                          │
│  │    }                            │                          │
│  │                                 │                          │
│  │    // Collect collisions        │                          │
│  │    for collision in collisions {│                          │
│  │      emit CollisionEvent        │                          │
│  │    }                            │                          │
│  │  }                              │                          │
│  └─────────────────────────────────┘                          │
└───────────────────────────────────────────────────────────────┘
```

**Key Characteristics**:
- ✅ Physics emits events (CollisionEvent, PositionUpdateEvent, VelocityChangeEvent)
- ✅ Application layer subscribes to events
- ✅ Application layer translates events into domain operations
- ✅ Domain entities remain unaware of physics
- ✅ Maintains Clean Architecture dependency flow

### Pattern 2: Synchronization Manager (BIDIRECTIONAL)

**Principle**: A middle layer orchestrates two-way sync between entities and physics.

```
┌──────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                              │
│                                                               │
│  ┌─────────────┐     ┌─────────────┐                        │
│  │   Entity    │     │   Entity    │                        │
│  │  position   │     │  position   │                        │
│  │  velocity   │     │  velocity   │                        │
│  └─────────────┘     └─────────────┘                        │
│         ▲                   ▲                                │
└─────────┼───────────────────┼────────────────────────────────┘
          │                   │
          │                   │
┌─────────┼───────────────────┼────────────────────────────────┐
│         │ PRESENTATION LAYER│                                │
│         │ (Synchronization) │                                │
│         │                   │                                │
│  ┌──────┴───────────────────┴──────┐                        │
│  │  PhysicsSyncManager              │                        │
│  │                                  │                        │
│  │  syncToPhysics() {               │  Called before physics │
│  │    // Entity → Physics           │  simulation           │
│  │    for entity in entities {      │                        │
│  │      if (!entity.isKinematic)    │                        │
│  │        continue  // Skip          │                        │
│  │      body = getBody(entity)      │                        │
│  │      body.setPosition(           │                        │
│  │        entity.position)          │                        │
│  │    }                             │                        │
│  │  }                               │                        │
│  │                                  │                        │
│  │  syncFromPhysics() {             │  Called after physics  │
│  │    // Physics → Entity           │  simulation           │
│  │    for entity in entities {      │                        │
│  │      if (entity.isKinematic)     │                        │
│  │        continue  // Skip          │                        │
│  │      body = getBody(entity)      │                        │
│  │      entity.setPosition(         │                        │
│  │        body.getPosition())       │                        │
│  │      entity.setVelocity(         │                        │
│  │        body.getVelocity())       │                        │
│  │    }                             │                        │
│  │  }                               │                        │
│  └──────────────┬───────────────────┘                        │
│                 │                                             │
└─────────────────┼─────────────────────────────────────────────┘
                  │
┌─────────────────┼─────────────────────────────────────────────┐
│  INFRASTRUCTURE │LAYER (Physics)                              │
│                 │                                             │
│  ┌──────────────▼─────────────────┐                          │
│  │    PhysicsSystem                │                          │
│  │                                 │                          │
│  │  btDynamicsWorld                │                          │
│  │  List<btRigidBody>              │                          │
│  └─────────────────────────────────┘                          │
└───────────────────────────────────────────────────────────────┘
```

**Game Loop**:
```java
void gameLoop(float deltaTime) {
    // 1. Game logic updates kinematic entities (player input, AI decisions)
    gameLogic.update(deltaTime);  // Updates entity.position directly
    
    // 2. Sync kinematic entities TO physics
    physicsSyncManager.syncToPhysics();  // Player position → btRigidBody
    
    // 3. Run physics simulation
    physicsSystem.stepSimulation(deltaTime);  // Calculates new positions
    
    // 4. Sync dynamic entities FROM physics
    physicsSyncManager.syncFromPhysics();  // btRigidBody → Entity position
    
    // 5. Render based on final entity positions
    renderingSystem.render(deltaTime);
}
```

**Key Characteristics**:
- ✅ Clear separation between kinematic (game-controlled) and dynamic (physics-controlled) entities
- ✅ Bidirectional sync happens in presentation layer
- ✅ Domain entities still unaware of physics implementation
- ✅ Simple, predictable control flow

### Pattern 3: Command Pattern

**Principle**: Physics generates commands that application layer executes on entities.

```java
// Infrastructure Layer - Physics System generates commands
interface PhysicsCommand {
    void execute(Entity entity);
}

class SetPositionCommand implements PhysicsCommand {
    private Vector3 newPosition;
    
    public void execute(Entity entity) {
        entity.setPosition(newPosition);
    }
}

class ApplyDamageCommand implements PhysicsCommand {
    private float damage;
    
    public void execute(Entity entity) {
        entity.takeDamage(damage);
    }
}

// Physics system after simulation
class PhysicsSystem {
    public List<PhysicsCommand> stepSimulation(float dt) {
        dynamicsWorld.stepSimulation(dt);
        
        List<PhysicsCommand> commands = new ArrayList<>();
        
        // Generate commands from physics results
        for (body : bodies) {
            if (body.hasMoved()) {
                commands.add(new SetPositionCommand(body.getPosition()));
            }
        }
        
        for (collision : getCollisions()) {
            if (isHarmfulCollision(collision)) {
                commands.add(new ApplyDamageCommand(calculateDamage()));
            }
        }
        
        return commands;
    }
}

// Application Layer - executes commands
class GameLoop {
    void update(float dt) {
        List<PhysicsCommand> commands = physicsSystem.stepSimulation(dt);
        
        for (command : commands) {
            Entity entity = getEntityForCommand(command);
            command.execute(entity);
        }
    }
}
```

**Key Characteristics**:
- ✅ Physics system doesn't directly modify entities
- ✅ Commands can be queued, logged, replayed
- ✅ Easy to implement undo/redo
- ⚠️ More complex than event-driven approach

## Recommended Architecture: Event-Driven with Sync Manager

**Hybrid approach combining Patterns 1 and 2:**

### Core Principle: Entity Ownership

**Kinematic Entities** (Game Logic Owns):
- Player (controlled by input)
- AI-controlled zombies (controlled by AI logic)
- Kinematic platforms, elevators

**Flow**: `Entity → Physics` (one-way)
- Game logic updates entity.position
- PhysicsSyncManager.syncToPhysics() updates btRigidBody position
- Physics uses kinematic bodies for collision detection only
- btRigidBody is marked as kinematic (infinite mass)

**Dynamic Entities** (Physics Owns):
- Ragdolls
- Projectiles (after firing)
- Physics-simulated objects (barrels, debris)

**Flow**: `Physics → Entity` (one-way)
- Physics simulation calculates position
- PhysicsSyncManager.syncFromPhysics() reads btRigidBody position
- Updates entity.position from physics result

**Hybrid Entities** (Both):
- Zombie initially kinematic (AI-controlled), becomes ragdoll on death (physics-controlled)
- Switch ownership via flag: `entity.isKinematic()`

### Implementation Example

```java
// Domain Layer - Entity with ownership flag
public class Zombie {
    private Vector3 position;
    private Vector3 velocity;
    private boolean kinematic = true;  // Initially AI-controlled
    
    public void die() {
        kinematic = false;  // Switch to physics-controlled ragdoll
    }
    
    // Game logic can update position when kinematic
    public void setPosition(Vector3 pos) {
        if (kinematic) {
            this.position = pos;
        }
        // Ignore if physics-controlled
    }
    
    // Physics can update position when dynamic
    public void applyPhysicsUpdate(Vector3 pos, Vector3 vel) {
        if (!kinematic) {
            this.position = pos;
            this.velocity = vel;
        }
        // Ignore if game-controlled
    }
}

// Presentation Layer - Bidirectional Sync Manager
public class PhysicsSyncManager {
    private Map<Entity, btRigidBody> entityBodyMap;
    private PhysicsSystem physicsSystem;
    
    // BEFORE physics simulation: Game logic → Physics
    public void syncToPhysics(List<Entity> entities) {
        for (Entity entity : entities) {
            if (!entity.isKinematic()) {
                continue;  // Skip dynamic entities
            }
            
            btRigidBody body = entityBodyMap.get(entity);
            
            // Update kinematic body position from entity
            btTransform transform = new btTransform();
            transform.setOrigin(entity.getPosition());
            body.setWorldTransform(transform);
            body.setLinearVelocity(entity.getVelocity());
        }
    }
    
    // AFTER physics simulation: Physics → Game logic
    public void syncFromPhysics(List<Entity> entities) {
        for (Entity entity : entities) {
            if (entity.isKinematic()) {
                continue;  // Skip kinematic entities
            }
            
            btRigidBody body = entityBodyMap.get(entity);
            
            // Read physics results
            btTransform transform = body.getWorldTransform();
            Vector3 newPos = transform.getOrigin();
            Vector3 newVel = body.getLinearVelocity();
            
            // Update entity with physics results
            entity.applyPhysicsUpdate(newPos, newVel);
        }
    }
    
    // Handle collision events
    public void processCollisionEvents(List<CollisionEvent> events) {
        for (CollisionEvent event : events) {
            Entity entityA = getEntity(event.bodyA);
            Entity entityB = getEntity(event.bodyB);
            
            // Application logic interprets collision
            if (entityA instanceof Player && entityB instanceof Zombie) {
                handlePlayerZombieCollision(entityA, entityB, event);
            }
            // ... more collision handlers
        }
    }
    
    private void handlePlayerZombieCollision(Entity player, Entity zombie, 
                                             CollisionEvent event) {
        // Translate physics event to domain operations
        float impactForce = event.getImpactForce();
        
        if (impactForce > DAMAGE_THRESHOLD) {
            player.takeDamage(calculateDamage(impactForce));
        }
        
        // Could also apply knockback to player
        Vector3 knockback = calculateKnockback(event);
        player.applyForce(knockback);
    }
}

// Application Layer - Game Loop
public class GameView {
    private PhysicsSystem physicsSystem;
    private PhysicsSyncManager physicsSyncManager;
    private List<Entity> entities;
    
    public void update(float deltaTime) {
        // 1. Game logic updates kinematic entities
        updateGameLogic(deltaTime);  // AI moves zombies, player input, etc.
        
        // 2. Sync kinematic entities to physics
        physicsSyncManager.syncToPhysics(entities);
        
        // 3. Step physics simulation
        List<CollisionEvent> collisions = physicsSystem.stepSimulation(deltaTime);
        
        // 4. Sync dynamic entities from physics
        physicsSyncManager.syncFromPhysics(entities);
        
        // 5. Process collision events
        physicsSyncManager.processCollisionEvents(collisions);
        
        // 6. Sync all entities to rendering
        renderingSyncManager.updateAll(entities);
        
        // 7. Render
        renderingSystem.render(deltaTime);
    }
}
```

## Key Architectural Decisions

### 1. Entity State Management

**Decision**: Entity is the single source of truth for logical state.

**Rationale**:
- Physics bodies are just infrastructure
- If physics crashes/restarts, entity state can recreate physics bodies
- Game saves contain entity data, not physics data
- Easier to test entity logic without physics

**Implementation**:
```java
public class Entity {
    // Authoritative state
    private Vector3 position;
    private Vector3 velocity;
    
    // Control flag
    private boolean kinematic;
    
    // Physics can only update when not kinematic
    public void applyPhysicsUpdate(Vector3 pos, Vector3 vel) {
        if (!kinematic) {
            this.position = pos;
            this.velocity = vel;
        }
    }
}
```

### 2. Collision Event Handling

**Decision**: Use event-driven pattern for collision response.

**Rationale**:
- Separates collision detection (infrastructure) from collision response (application)
- Application layer decides what collisions mean
- Easy to add new collision types without modifying physics

**Implementation**:
```java
// Infrastructure Layer
public class CollisionEvent {
    public final btRigidBody bodyA;
    public final btRigidBody bodyB;
    public final Vector3 contactPoint;
    public final Vector3 contactNormal;
    public final float impulse;
}

// Application Layer
public interface CollisionHandler {
    boolean canHandle(Entity a, Entity b);
    void handle(Entity a, Entity b, CollisionEvent event);
}

public class PlayerZombieCollisionHandler implements CollisionHandler {
    public boolean canHandle(Entity a, Entity b) {
        return (a instanceof Player && b instanceof Zombie) ||
               (a instanceof Zombie && b instanceof Player);
    }
    
    public void handle(Entity a, Entity b, CollisionEvent event) {
        Player player = (a instanceof Player) ? (Player)a : (Player)b;
        Zombie zombie = (a instanceof Zombie) ? (Zombie)a : (Zombie)b;
        
        // Domain logic
        if (event.impulse > DAMAGE_THRESHOLD) {
            player.takeDamage(zombie.getAttackDamage());
        }
    }
}

// Sync Manager uses handlers
public class PhysicsSyncManager {
    private List<CollisionHandler> collisionHandlers;
    
    public void processCollisionEvents(List<CollisionEvent> events) {
        for (CollisionEvent event : events) {
            Entity a = getEntity(event.bodyA);
            Entity b = getEntity(event.bodyB);
            
            for (CollisionHandler handler : collisionHandlers) {
                if (handler.canHandle(a, b)) {
                    handler.handle(a, b, event);
                    break;
                }
            }
        }
    }
}
```

### 3. Update Order

**Decision**: Sync kinematic entities TO physics before simulation, sync dynamic entities FROM physics after.

**Order**:
```
1. Game Logic (updates kinematic entities)
   └─> player.setPosition() via input
   └─> zombie.setPosition() via AI

2. Sync TO Physics (kinematic only)
   └─> btRigidBody.setWorldTransform(player.position)

3. Physics Simulation
   └─> btDynamicsWorld.stepSimulation()
   └─> Calculates ragdoll positions, projectile positions

4. Sync FROM Physics (dynamic only)
   └─> ragdoll.setPosition(btRigidBody.position)

5. Process Collision Events
   └─> player.takeDamage() if hit by zombie

6. Sync to Rendering (all entities)
   └─> Scene.transform = entity.position

7. Render
```

## Migration from Current btCollisionWorld

### Current State
- `btCollisionWorld` - collision detection only, no simulation
- `btCollisionObject` - static collision shapes
- No position updates from physics

### Target State
- `btDynamicsWorld` - full physics simulation
- `btRigidBody` - dynamic bodies with mass, velocity, forces
- Bidirectional sync between entities and physics

### Migration Steps

**Phase 1: Add Dynamics World (Parallel)**
```java
// Keep old system running
CollisionHandler oldCollisionHandler;  // btCollisionWorld

// Add new system
PhysicsSystem physicsSystem;  // btDynamicsWorld
PhysicsSyncManager physicsSyncManager;

// Feature flag to switch
boolean usePhysicsSimulation = false;
```

**Phase 2: Add Entity Ownership Flags**
```java
public class Zombie {
    private boolean kinematic = true;  // Add flag
    
    // Add method to switch
    public void die() {
        kinematic = false;  // Become ragdoll
    }
}
```

**Phase 3: Implement Sync Manager**
```java
PhysicsSyncManager syncManager = new PhysicsSyncManager(physicsSystem);

// In game loop
if (usePhysicsSimulation) {
    syncManager.syncToPhysics(entities);
    physicsSystem.stepSimulation(deltaTime);
    syncManager.syncFromPhysics(entities);
} else {
    oldCollisionHandler.checkCollision();
}
```

**Phase 4: Migrate Entity by Entity**
- Start with simple entities (projectiles)
- Then dynamic props (barrels)
- Then ragdolls
- Finally kinematic entities (zombies, player)

**Phase 5: Remove Old System**
- Delete `CollisionHandler` with `btCollisionWorld`
- Remove `GameMesh` coupling
- Clean up

## Benefits of This Architecture

### Clean Architecture Compliance
- ✅ Entities remain in domain layer, unaware of physics
- ✅ Physics is infrastructure concern
- ✅ Sync manager is presentation layer (orchestration)
- ✅ Collision response is application layer (business logic)

### SOLID Principles
- ✅ **SRP**: PhysicsSystem does physics, SyncManager does syncing, CollisionHandlers handle collisions
- ✅ **DIP**: All depend on Entity interface, not concrete physics types
- ✅ **OCP**: New collision types via new CollisionHandler implementations
- ✅ **LSP**: Any Entity can be substituted
- ✅ **ISP**: Entities only implement needed interfaces (Collidable is optional)

### Practical Benefits
- ✅ Clear ownership model (kinematic vs dynamic)
- ✅ Easy to debug (clear data flow)
- ✅ Testable (can test game logic without physics)
- ✅ Flexible (can switch between game-controlled and physics-controlled)
- ✅ Efficient (sync only what's needed, when needed)

## Code Example: Complete Flow

```java
// Game Loop showing complete bidirectional flow
public class GameView {
    private List<Zombie> zombies;
    private Player player;
    private PhysicsSystem physicsSystem;
    private PhysicsSyncManager physicsSyncManager;
    
    public void update(float deltaTime) {
        // === GAME LOGIC UPDATES (Entity Layer) ===
        
        // AI updates zombie positions (kinematic)
        for (Zombie zombie : zombies) {
            if (zombie.isKinematic()) {
                Vector3 targetPos = zombie.calculateAIMovement(player, deltaTime);
                zombie.setPosition(targetPos);  // Entity owns position
            }
        }
        
        // Player input (kinematic)
        Vector3 inputVelocity = gameInputAdapter.getMovementInput();
        player.updatePosition(inputVelocity, deltaTime);  // Entity owns position
        
        // === SYNC TO PHYSICS (Presentation Layer) ===
        
        List<Entity> allEntities = new ArrayList<>();
        allEntities.add(player);
        allEntities.addAll(zombies);
        
        // Push kinematic entity positions to physics
        physicsSyncManager.syncToPhysics(allEntities);
        
        // === PHYSICS SIMULATION (Infrastructure Layer) ===
        
        // Run physics (calculates new positions for dynamic entities)
        List<CollisionEvent> collisions = physicsSystem.stepSimulation(deltaTime);
        
        // === SYNC FROM PHYSICS (Presentation Layer) ===
        
        // Pull dynamic entity positions from physics
        physicsSyncManager.syncFromPhysics(allEntities);
        
        // Process collision events (translate to domain operations)
        physicsSyncManager.processCollisionEvents(collisions);
        
        // === RENDERING SYNC (Presentation Layer) ===
        
        renderingSyncManager.updateAll(allEntities);
        
        // === RENDER (Infrastructure Layer) ===
        
        renderingSystem.render(deltaTime);
    }
}
```

## Summary

**The key insight**: Physics and entities don't directly communicate. Instead:

1. **PhysicsSyncManager** sits in the presentation layer
2. **Before simulation**: Syncs kinematic entities → physics
3. **After simulation**: Syncs dynamic entities ← physics
4. **Collision events**: Interpreted by application layer, applied to entities
5. **Entity owns its state**: Physics is just a service that calculates positions

This maintains:
- Clean Architecture (dependencies flow inward)
- Entity purity (no framework dependencies)
- Clear control flow (explicit sync points)
- Flexibility (easy to switch between kinematic and dynamic)

The entity layer never knows about Bullet Physics. The physics layer never directly modifies entities. The sync manager orchestrates everything from the presentation layer, keeping concerns properly separated.
