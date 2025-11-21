# Architecture Diagrams

## Current Dual Rendering System

```
┌─────────────────────────────────────────────────────────────────┐
│                        ObjectRenderer                            │
│  ┌─────────────────────────────┬────────────────────────────┐  │
│  │     ModelBatch System       │    SceneManager System     │  │
│  │   (Chunks - Solid Colors)   │  (Models - Textured GLTF)  │  │
│  └──────────────┬──────────────┴──────────────┬─────────────┘  │
└─────────────────┼───────────────────────────────┼────────────────┘
                  │                               │
                  ▼                               ▼
         ┌────────────────┐              ┌────────────────┐
         │  ModelInstance │              │     Scene      │
         │  + GameMesh    │              │  (from GLTF)   │
         │  + ChunkMesh   │              │                │
         └────────┬───────┘              └────────┬───────┘
                  │                               │
         ┌────────▼───────┐              ┌────────▼───────┐
         │ ColorAttribute │              │TextureAttribute│
         │  (solid color) │              │  (textures)    │
         └────────────────┘              └────────────────┘
```

## Data Flow - Chunk Rendering (Current)

```
┌──────────┐    ┌──────────────────────┐    ┌──────────────┐
│  Chunk   │───▶│ChunkMeshGeneration   │───▶│ChunkMeshData │
│ (domain) │    │    Interactor        │    │(extends      │
└──────────┘    │  (use case)          │    │ GameMesh)    │
                └──────────────────────┘    └──────┬───────┘
                         │                          │
                         │ Uses                     │ Added to
                         ▼                          ▼
                ┌─────────────────┐        ┌───────────────┐
                │BlockMaterial    │        │ObjectRenderer │
                │  Repository     │        │               │
                │(solid colors)   │        │  ModelBatch   │
                └─────────────────┘        └───────┬───────┘
                                                    │
                                                    ▼
                                           ┌────────────────┐
                                           │  GPU Render    │
                                           │ (solid colors) │
                                           └────────────────┘
```

## Data Flow - Textured Model Rendering (Current)

```
┌──────────┐    ┌──────────┐    ┌───────────┐    ┌──────────────┐
│GLTF File │───▶│GLTFLoader│───▶│SceneAsset │───▶│    Scene     │
└──────────┘    └──────────┘    └───────────┘    └──────┬───────┘
                                                          │
                                                          │ Added to
                                                          ▼
                                                 ┌────────────────┐
                                                 │ObjectRenderer  │
                                                 │                │
                                                 │ SceneManager   │
                                                 └────────┬───────┘
                                                          │
                                                          ▼
                                                 ┌────────────────┐
                                                 │  GPU Render    │
                                                 │  (textures)    │
                                                 └────────────────┘
```

## Proposed Architecture - Unified Scene Rendering

```
┌─────────────────────────────────────────────────────────────┐
│                    ObjectRenderer                            │
│              (Single SceneManager System)                    │
│  ┌────────────────────────────────────────────────────┐    │
│  │           SceneManager (Unified)                   │    │
│  │  • Chunks (textured via atlas)                     │    │
│  │  • Models (GLTF with textures)                     │    │
│  │  • Physics objects                                 │    │
│  └────────────────────────────────────────────────────┘    │
└──────────────────────────┬──────────────────────────────────┘
                           │
              ┌────────────┴────────────┐
              │                         │
              ▼                         ▼
    ┌──────────────────┐      ┌──────────────────┐
    │  ChunkScene      │      │   ModelScene     │
    │ (from chunks)    │      │  (from GLTF)     │
    │                  │      │                  │
    │ • TextureAtlas   │      │ • Embedded       │
    │ • UV Mapping     │      │   Textures       │
    │ • Physics Data   │      │ • Animations     │
    └──────────────────┘      └──────────────────┘
```

## Texture Atlas Layout

```
┌─────────────────────────────────────────────┐
│         256x256 Texture Atlas               │
│  ┌───┬───┬───┬───┬───┬───┬───┬───┬───┐    │
│  │ 0 │ 1 │ 2 │ 3 │ 4 │ 5 │ 6 │ 7 │...│ ◄── Row 0
│  │Top│Sid│Drt│Stn│Cbl│Top│Sid│Lvs│   │    │
│  ├───┼───┼───┼───┼───┼───┼───┼───┼───┤    │
│  │16 │17 │18 │19 │20 │21 │22 │23 │...│ ◄── Row 1
│  │Snd│Grv│Ore│Ore│Ore│Ore│Ore│Brk│   │    │
│  ├───┼───┼───┼───┼───┼───┼───┼───┼───┤    │
│  │...│...│...│...│...│...│...│...│...│    │
│  └───┴───┴───┴───┴───┴───┴───┴───┴───┘    │
│                                             │
│  Each cell: 16x16 pixels                    │
│  16 cells per row/column = 256 textures max │
└─────────────────────────────────────────────┘

UV Calculation Example (texture index 5):
  atlasX = 5 % 16 = 5
  atlasY = 5 / 16 = 0
  u1 = 5/16 = 0.3125
  v1 = 0/16 = 0.0
  u2 = 6/16 = 0.375
  v2 = 1/16 = 0.0625
```

## Phase 1: Texture Atlas Implementation

```
BEFORE (Solid Colors):                 AFTER (Textured):
┌─────────────────┐                   ┌─────────────────┐
│ MeshPartBuilder │                   │ MeshPartBuilder │
│                 │                   │                 │
│ Attributes:     │                   │ Attributes:     │
│ • Position      │                   │ • Position      │
│ • Normal        │                   │ • Normal        │
│                 │  ═════════════>   │ • UV Coords ◄── NEW!
│                 │                   │                 │
│ Material:       │                   │ Material:       │
│ ColorAttribute  │                   │TextureAttribute │
│ (solid color)   │                   │ (atlas texture) │
└─────────────────┘                   └─────────────────┘
```

## Phase 2: Hybrid Scene Integration

```
┌──────────────────────────────────────────────────────┐
│              ChunkMeshData (Hybrid)                   │
│  ┌────────────────┐         ┌────────────────┐      │
│  │  ModelInstance │         │ Scene Wrapper  │      │
│  │  (physics)     │ ◄────── │ (rendering)    │      │
│  │                │  shares │                │      │
│  │ • Transform    │  Model  │ • Transform    │      │
│  │ • Collision    │         │ • Visible      │      │
│  └────────────────┘         └────────────────┘      │
└──────────────┬───────────────────────┬───────────────┘
               │                       │
               ▼                       ▼
    ┌──────────────────┐    ┌──────────────────┐
    │CollisionHandler  │    │  SceneManager    │
    │  (physics)       │    │  (rendering)     │
    └──────────────────┘    └──────────────────┘
```

## Phase 3: Composition Pattern (Final Architecture)

```
┌─────────────────────────────────────────────────────────┐
│                    ChunkMeshData                         │
│  ┌──────────────────┐         ┌──────────────────┐     │
│  │ RenderableChunk  │         │  PhysicsChunk    │     │
│  │ implements       │         │  implements      │     │
│  │  IRenderable     │         │  ICollidable     │     │
│  │                  │         │                  │     │
│  │ • Scene          │         │ • CollisionShape │     │
│  │ • Transform      │         │ • CollisionBody  │     │
│  └────────┬─────────┘         └────────┬─────────┘     │
└───────────┼─────────────────────────────┼───────────────┘
            │                             │
            ▼                             ▼
   ┌────────────────┐          ┌────────────────┐
   │  IRenderable   │          │  ICollidable   │
   │  interface     │          │  interface     │
   └────────┬───────┘          └────────┬───────┘
            │                           │
            │ Used by                   │ Used by
            ▼                           ▼
   ┌────────────────┐          ┌────────────────┐
   │ObjectRenderer  │          │CollisionHandler│
   │(rendering only)│          │(physics only)  │
   └────────────────┘          └────────────────┘
```

## Clean Architecture Layers

```
┌───────────────────────────────────────────────────────────┐
│                    Presentation                            │
│              (ViewManager, GameView)                       │
└─────────────────────────┬─────────────────────────────────┘
                          │ Uses
                          ▼
┌───────────────────────────────────────────────────────────┐
│                Interface Adapters                          │
│  ┌──────────────────────────────────────────────────┐    │
│  │  Infrastructure (Rendering)                       │    │
│  │  • ObjectRenderer (uses SceneManager)             │    │
│  │  • LibGDXMaterialRepository (uses TextureAtlas)   │    │
│  │  • ChunkLoader                                    │    │
│  └──────────────────────────────────────────────────┘    │
└─────────────────────────┬─────────────────────────────────┘
                          │ Implements
                          ▼
┌───────────────────────────────────────────────────────────┐
│                     Use Cases                              │
│  ┌──────────────────────────────────────────────────┐    │
│  │  Ports (Interfaces)                               │    │
│  │  • BlockMaterialRepository ◄── Should be here!    │    │
│  │  • IRenderable, ICollidable ◄── Future           │    │
│  └──────────────────────────────────────────────────┘    │
│  ┌──────────────────────────────────────────────────┐    │
│  │  Interactors                                      │    │
│  │  • ChunkMeshGenerationInteractor                 │    │
│  └──────────────────────────────────────────────────┘    │
└─────────────────────────┬─────────────────────────────────┘
                          │ Uses
                          ▼
┌───────────────────────────────────────────────────────────┐
│                      Domain                                │
│  • Chunk (pure data)                                       │
│  • Block (pure data)                                       │
│  • World (pure data)                                       │
│  NO dependencies on outer layers                           │
└───────────────────────────────────────────────────────────┘
```

## Dependency Flow (Correct vs Current)

```
CURRENT (Violation):                CORRECT (Fixed):

Infrastructure                      Application (Use Cases)
     │                                     │
     │ contains                            │ defines
     ▼                                     ▼
BlockMaterial                         BlockMaterial
Repository ◄─────────────────┐       Repository ◄────────────┐
(interface)                  │       (interface/port)        │
     │                       │            │                  │
     │ implemented by        │ used by    │ implements       │ used by
     ▼                       │            ▼                  │
LibGDXMaterial               │       LibGDXMaterial          │
Repository                   │       Repository              │
                             │       (Infrastructure)        │
                             │                               │
                    ChunkMeshGeneration              ChunkMeshGeneration
                    Interactor                       Interactor
                    (Use Case)                       (Use Case)

❌ Use case depends on               ✅ Use case depends on
   infrastructure interface             its own interface
```

## Performance Comparison

```
Rendering Approach Comparison (100 chunks, 60 FPS target):

┌──────────────────────┬─────────────┬──────────────┬─────────────┐
│     Approach         │ Draw Calls  │ Texture      │ Performance │
│                      │  per Frame  │ Memory       │   Rating    │
├──────────────────────┼─────────────┼──────────────┼─────────────┤
│ Current (Solid)      │    100      │   0 MB       │    ★★★★★    │
│                      │             │              │   (base)    │
├──────────────────────┼─────────────┼──────────────┼─────────────┤
│ Per-Block Textures   │  100-300    │  ~10 MB      │    ★★☆☆☆    │
│                      │ (one per    │ (100 textures│  (poor)     │
│                      │  material)  │  @ 16x16)    │             │
├──────────────────────┼─────────────┼──────────────┼─────────────┤
│ Texture Atlas        │    100      │  0.25 MB     │    ★★★★★    │
│ (Recommended)        │ (one per    │ (1 atlas     │  (optimal)  │
│                      │  chunk)     │  256x256)    │             │
├──────────────────────┼─────────────┼──────────────┼─────────────┤
│ Scene-based          │    100      │  0.25 MB     │    ★★★★★    │
│ (Future)             │ (batched by │ (same atlas) │  (optimal)  │
│                      │ SceneManager│              │             │
└──────────────────────┴─────────────┴──────────────┴─────────────┘

Notes:
• Draw calls remain similar across approaches
• Texture atlas provides massive memory savings
• SceneManager adds minimal overhead (<1ms per frame)
• Unified rendering enables future optimizations (instancing, etc.)
```

---

**Diagram Version:** 1.0  
**Last Updated:** 2025-11-21  
**For:** Rendering System Consolidation Research
