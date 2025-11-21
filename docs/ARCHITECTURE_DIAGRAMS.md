# Architecture Visualization

## Current State: Three Fragmented Systems

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│                      (GameView.java)                         │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   System 1   │  │   System 2   │  │   System 3   │      │
│  │  Collision   │  │ Chunk Mesh   │  │Scene Render  │      │
│  │              │  │  Rendering   │  │    (.gltf)   │      │
│  │ Collision-   │  │              │  │              │      │
│  │  Handler     │◄─┤ModelBatch    │  │SceneManager  │      │
│  │              │  │              │  │              │      │
│  │btCollision-  │  │GameMesh ◄────┼──┤Scene         │      │
│  │  Object      │  │(COUPLED!)    │  │              │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         ▲                 ▲                  ▲              │
└─────────┼─────────────────┼──────────────────┼──────────────┘
          │                 │                  │
          │    COUPLING!    │                  │
          │   GameMesh has  │                  │
          │  BOTH render &  │                  │
          │  collision data │                  │
          │                 │                  │
┌─────────┼─────────────────┼──────────────────┼──────────────┐
│         │    DOMAIN LAYER │                  │              │
│         │                 │                  │              │
│    ┌────┴────┐       ┌────┴────┐       ┌────┴────┐         │
│    │ Player  │       │  Chunk  │       │ Zombie  │         │
│    │         │       │         │       │         │         │
│    │position │       │blocks   │       │position │         │
│    │direction│       │         │       │health   │         │
│    └─────────┘       └─────────┘       └─────────┘         │
│                                                             │
└─────────────────────────────────────────────────────────────┘

PROBLEMS:
❌ GameMesh couples rendering + collision (violates SRP)
❌ ObjectRenderer manages both rendering AND collision (violates SRP)
❌ Transparency bug: ModelBatch and SceneManager render separately
❌ No unified entity abstraction
❌ ZombieInstanceUpdater is zombie-specific, not generalized
```

## Target State: Two Unified Systems with Updater Layer

```
┌─────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                           │
│                      (GameView.java)                             │
│                                                                  │
│              ┌────────────────────────────┐                     │
│              │ EntitySynchronizationMgr   │                     │
│              │      (Updater Layer)        │                     │
│              │                             │                     │
│              │  ┌──────────────────────┐  │                     │
│              │  │EntityRenderUpdater   │  │                     │
│              │  │EntityCollisionUpdater│  │                     │
│              │  └──────────────────────┘  │                     │
│              └──────┬──────────────┬──────┘                     │
│                     │              │                            │
│         ┌───────────▼──┐       ┌───▼──────────┐                │
│         │   System 1    │       │   System 2   │                │
│         │   RENDERING   │       │  COLLISION   │                │
│         │               │       │              │                │
│         │ SceneManager  │       │btDynamics-   │                │
│         │   (unified)   │       │   World      │                │
│         │               │       │              │                │
│         │ Handles:      │       │ Handles:     │                │
│         │ - Chunks      │       │ - Static     │                │
│         │ - Entities    │       │ - Dynamic    │                │
│         │ - Transparency│       │ - Triggers   │                │
│         └───────────────┘       └──────────────┘                │
│                 ▲                       ▲                        │
│                 │                       │                        │
│        ┌────────┴────────┐     ┌────────┴────────┐             │
│        │RenderableEntity │     │CollidableEntity │             │
│        │   (interface)   │     │   (interface)   │             │
│        └─────────────────┘     └─────────────────┘             │
└─────────────────────────────────────────────────────────────────┘
                         │                       │
                         │   ADAPTERS BRIDGE     │
                         │   TO DOMAIN LAYER     │
                         │                       │
┌────────────────────────┼───────────────────────┼────────────────┐
│              DOMAIN LAYER (Pure Business Logic)                 │
│                          │                     │                │
│                    ┌─────▼─────┐         ┌────▼─────┐          │
│                    │  Player   │         │  Zombie  │          │
│                    │           │         │          │          │
│                    │ position  │         │ position │          │
│                    │ direction │         │ health   │          │
│                    │ inventory │         │ speed    │          │
│                    └───────────┘         └──────────┘          │
│                          │                     │                │
│                          │                     │                │
│                    ┌─────▼─────────────────────▼──────┐        │
│                    │          Chunk                    │        │
│                    │                                   │        │
│                    │  blocks[][][]                     │        │
│                    │  chunkCoordinates                 │        │
│                    └───────────────────────────────────┘        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

BENEFITS:
✅ Two unified systems (rendering, collision)
✅ Proper separation of concerns (SOLID compliant)
✅ Entities unaware of infrastructure
✅ Updater layer synchronizes everything
✅ Transparency rendering works correctly
✅ Extensible for new entity types
```

## Data Flow: How Entity Updates Propagate

```
GAME LOGIC UPDATES ENTITY:
  
  ┌─────────────────┐
  │ Game Logic      │
  │ (Use Case)      │
  └────────┬────────┘
           │
           │ 1. Update entity
           ▼
  ┌─────────────────┐
  │ Zombie          │
  │                 │
  │ setPosition(x)  │ ◄── Domain layer knows nothing
  │                 │     about rendering or collision
  └────────┬────────┘
           │
           │ 2. Entity state changed
           ▼
  ┌─────────────────────────────────┐
  │ EntitySynchronizationManager    │
  │ (Updater Layer)                 │
  │                                 │
  │ Observes entity changes and     │
  │ updates infrastructure systems  │
  └────┬─────────────────────┬──────┘
       │                     │
       │ 3a. Update render   │ 3b. Update collision
       ▼                     ▼
  ┌─────────────┐       ┌──────────────┐
  │ Scene       │       │ btRigidBody  │
  │ transform   │       │ transform    │
  │             │       │              │
  │ setTrans-   │       │ setWorld-    │
  │ lation(x)   │       │ Transform(x) │
  └─────────────┘       └──────────────┘
       │                     │
       │ 4a. Visual update   │ 4b. Physics update
       ▼                     ▼
  ┌─────────────┐       ┌──────────────┐
  │ Rendering   │       │ Collision    │
  │ System      │       │ System       │
  │             │       │              │
  │ renders at  │       │ detects      │
  │ position x  │       │ collisions   │
  └─────────────┘       └──────────────┘

KEY PRINCIPLE: 
Entity position is the SINGLE SOURCE OF TRUTH.
Infrastructure systems (rendering, collision) are DERIVED STATE.
Updater layer keeps them synchronized.
```

## Package Structure: Current vs Proposed

### Current Structure:
```
core/src/main/java/
├── domain/entities/           ✅ Good - pure domain
│   ├── Player.java
│   ├── Zombie.java
│   └── Chunk.java
├── application/use_cases/     ✅ Good - clean use cases
│   ├── EntityGeneration/
│   └── RenderZombie/
├── physics/                   ❌ Confusing - should be infrastructure
│   ├── CollisionHandler.java
│   ├── GameMesh.java          ❌ Couples render + collision
│   └── HitBox.java
├── infrastructure/rendering/  ⚠️ Mixed - has both systems
│   ├── ObjectRenderer.java    ❌ Manages collision too
│   ├── ChunkLoader.java
│   └── ModelGeneratorFacade.java
└── presentation/
    ├── ZombieInstanceUpdater.java  ⚠️ Zombie-specific
    └── view/
        └── GameView.java
```

### Proposed Structure:
```
core/src/main/java/
├── domain/entities/                    ✅ Unchanged - pure domain
│   ├── Player.java
│   ├── Zombie.java
│   └── Chunk.java
├── application/use_cases/              ✅ Unchanged - clean use cases
│   ├── EntityGeneration/
│   └── RenderZombie/
├── infrastructure/                     ✅ Reorganized
│   ├── rendering/                      ✅ Rendering only
│   │   ├── RenderingSystem.java        (wraps SceneManager)
│   │   ├── RenderableEntity.java       (interface)
│   │   ├── RenderMesh.java             (separated from collision)
│   │   └── ModelAssetLoader.java
│   └── collision/                      ✅ Renamed from physics
│       ├── CollisionSystem.java        (wraps btDynamicsWorld)
│       ├── CollidableEntity.java       (interface)
│       ├── CollisionBody.java          (separated from rendering)
│       └── CollisionEventDispatcher.java
└── presentation/
    ├── updaters/                       ✅ New - generalized updater layer
    │   ├── EntitySynchronizationManager.java
    │   ├── EntityRenderUpdater.java
    │   └── EntityCollisionUpdater.java
    └── view/
        └── GameView.java               (orchestrates everything)
```

## Transparency Rendering: Problem vs Solution

### Current Problem:
```
Frame Rendering Order in ObjectRenderer.render():

Step 1: sceneManager.render()
  ┌────────────────────────────┐
  │ Zombie (Scene, alpha=0.5)  │  Writes depth buffer
  └────────────────────────────┘  even though transparent

Step 2: modelBatch.render()
  ┌────────────────────────────┐
  │ Chunk (ModelInstance)      │  Fails depth test!
  │                            │  Not visible behind zombie
  │ ❌ Culled by depth buffer  │
  └────────────────────────────┘

RESULT: Chunks disappear behind transparent zombies
```

### Solution:
```
Unified Rendering in SceneManager:

Step 1: Collect all Scene objects (chunks + entities)
  ┌────────────────────────────┐
  │ Chunk (as Scene)           │
  └────────────────────────────┘
  ┌────────────────────────────┐
  │ Zombie (as Scene)          │
  └────────────────────────────┘

Step 2: SceneManager sorts by depth and transparency
  - Opaque objects front-to-back (depth optimization)
  - Transparent objects back-to-front (correct blending)

Step 3: Single unified render pass
  ┌────────────────────────────┐
  │ 1. Opaque chunks (nearest) │
  ├────────────────────────────┤
  │ 2. Opaque chunks (farther) │
  ├────────────────────────────┤
  │ 3. Transparent zombie      │ ✅ Chunks visible behind!
  └────────────────────────────┘

RESULT: Transparency works correctly
```

## Dependency Flow: Clean Architecture Compliance

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                    │
│                                                          │
│  GameView (coordinates everything)                      │
│    │                                                     │
│    ├─► EntitySynchronizationManager                     │
│    │                                                     │
│    ├─► Infrastructure Systems (implementations)         │
│    │   ├─► RenderingSystem (SceneManager wrapper)       │
│    │   └─► CollisionSystem (btDynamicsWorld wrapper)    │
└────┼──────────────────────────────────────────────────┬──┘
     │                                                  │
     │ depends on ▼                                     │ implements ▼
     │                                                  │
┌────┼──────────────────────────────────────────────────┼──┐
│    │              APPLICATION LAYER                   │  │
│    │                                                  │  │
│    ├─► Use Case Interactors                          │  │
│    │   ├─► EntityGenerationInteractor                │  │
│    │   └─► RenderZombieInteractor                    │  │
│    │                                                  │  │
│    └─► Ports (interfaces)                           ◄┼──┘
│        ├─► RenderableEntity (interface)              │
│        ├─► CollidableEntity (interface)              │
│        └─► BlockRepository (interface)               │
└────┼─────────────────────────────────────────────────────┘
     │
     │ depends on ▼
     │
┌────┼─────────────────────────────────────────────────────┐
│    │                 DOMAIN LAYER                        │
│    │                                                     │
│    └─► Entities (pure business logic)                   │
│        ├─► Player                                        │
│        ├─► Zombie                                        │
│        ├─► Chunk                                         │
│        └─► World                                         │
│                                                          │
│    NO DEPENDENCIES ON OUTER LAYERS ✅                    │
└──────────────────────────────────────────────────────────┘

DEPENDENCY RULE:
Dependencies point INWARD only.
Domain has zero dependencies.
Application depends on domain.
Presentation/Infrastructure depend on application and domain.

CURRENT VIOLATIONS FIXED:
❌ ObjectRenderer depending on CollisionHandler directly
   → Both depend on application layer interfaces

❌ GameMesh coupling rendering and collision
   → Separated into RenderMesh and CollisionBody

❌ Physics package used by infrastructure
   → Renamed to infrastructure.collision
```

## Summary: Transformation Benefits

| Aspect | Current State | Target State |
|--------|---------------|--------------|
| **Rendering** | 2 systems (ModelBatch + SceneManager) | 1 unified system (SceneManager) |
| **Collision** | Coupled with rendering (GameMesh) | Independent system (CollisionBody) |
| **Transparency** | ❌ Broken (chunks invisible) | ✅ Fixed (proper depth sorting) |
| **Entity Awareness** | ❌ Some coupling (rendered flag) | ✅ Completely unaware |
| **SRP Compliance** | ❌ Multiple violations | ✅ Compliant |
| **DIP Compliance** | ❌ Concrete dependencies | ✅ Depends on abstractions |
| **Extensibility** | ⚠️ Requires modifying multiple files | ✅ Implement interfaces only |
| **Updater Pattern** | ⚠️ Partial (ZombieInstanceUpdater) | ✅ Generalized (EntitySyncMgr) |
| **Package Structure** | ⚠️ Confusing (physics?) | ✅ Clear (infrastructure.*) |
| **Testing** | ❌ Hard (coupled dependencies) | ✅ Easy (mocked interfaces) |

The transformation from three fragmented systems to two unified systems with a proper updater layer achieves all requirements while maintaining Clean Architecture principles.
