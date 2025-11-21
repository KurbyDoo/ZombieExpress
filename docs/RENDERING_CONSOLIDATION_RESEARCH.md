# Rendering System Consolidation Research

## Executive Summary

This document provides comprehensive research on consolidating the dual rendering systems currently used in ZombieExpress into a unified Scene-based approach with texture mapping support for voxel chunks.

**Current State:**
- **System 1**: ModelBatch + ModelInstance for chunk rendering (solid colors only)
- **System 2**: SceneManager + Scene for textured model rendering (GLTF with textures)

**Goal:**
Consolidate to SceneManager + Scene for all rendering, enabling texture mapping on chunk meshes.

---

## Current Architecture Analysis

### System 1: Chunk Rendering (ModelInstance-based)

**Data Flow:**
```
Chunk (domain) 
  → ChunkMeshGenerationInteractor 
  → ChunkMeshData (extends GameMesh/ModelInstance)
  → ObjectRenderer.add()
  → ModelBatch.render()
```

**Key Components:**
- **ChunkMeshGenerationInteractor**: Generates mesh geometry using LibGDX ModelBuilder
- **Vertex Attributes**: Position + Normal (NO UV coordinates)
- **Materials**: Solid colors via LibGDXMaterialRepository (ColorAttribute)
- **Rendering**: ModelBatch with Environment lighting
- **Physics**: Bullet Physics collision shapes integrated

**Limitations:**
- No texture support
- Only solid color materials
- Separate rendering pipeline from textured objects

### System 2: Scene Rendering (GLTF-based)

**Data Flow:**
```
GLTF file 
  → GLTFLoader 
  → SceneAsset 
  → Scene
  → SceneManager.addScene()
  → SceneManager.render()
```

**Key Components:**
- **GLTFLoader**: Loads GLTF files with embedded textures
- **Scene**: Wrapper around ModelInstance with additional features
- **SceneManager**: Advanced rendering system with shader support
- **Materials**: Full PBR support from GLTF (textures, roughness, metalness)

**Advantages:**
- Full texture support
- PBR materials
- Unified rendering pipeline potential

---

## Texture Mapping Research for Chunks

### Approach 1: Texture Atlas with UV Mapping ⭐ **RECOMMENDED**

**Description:**
Create a single texture atlas containing all block textures in a grid layout. Each block face gets UV coordinates that map to its specific region in the atlas.

**Pros:**
✅ Best performance (single texture binding, minimal draw calls)
✅ Industry standard for voxel games (Minecraft, Minetest)
✅ Scales well with many block types
✅ LibGDX ModelBuilder natively supports UV coordinates
✅ Compatible with current ModelInstance approach (gradual migration)

**Cons:**
❌ Requires texture atlas creation and maintenance
❌ UV calculation complexity for each face
❌ Atlas size limits number of unique textures
❌ Changing one texture requires atlas rebuild

**Implementation Steps:**

1. **Create Texture Atlas**
   - Design: 256x256 texture with 16x16 blocks at 16x16 pixels each
   - Format: PNG with transparent background for later extensions
   - Tool: Can use LibGDX TexturePacker or manual creation

2. **Modify Vertex Attributes**
   ```java
   // In ChunkMeshGenerationInteractor.buildType()
   MeshPartBuilder meshBuilder = modelBuilder.part(
       type.toString(), 
       GL20.GL_TRIANGLES, 
       Usage.Position | Usage.Normal | Usage.TextureCoordinates,  // ADD UV
       material
   );
   ```

3. **Update Material Repository**
   ```java
   public class LibGDXMaterialRepository implements BlockMaterialRepository {
       private Texture blockAtlas;
       private Material atlasMaterial;
       
       public LibGDXMaterialRepository() {
           blockAtlas = new Texture(Gdx.files.internal("textures/block_atlas.png"));
           blockAtlas.setFilter(TextureFilter.Nearest, TextureFilter.Nearest); // Pixel art
           
           atlasMaterial = new Material(
               TextureAttribute.createDiffuse(blockAtlas)
           );
       }
       
       @Override
       public Material getMaterial(Block block) {
           return atlasMaterial;  // Same material, different UVs per face
       }
   }
   ```

4. **Calculate UV Coordinates**
   ```java
   // Add to ChunkMeshGenerationInteractor
   private void setUVForBlockFace(MeshPartBuilder meshBuilder, Block block, BlockFace face) {
       int textureIndex = getTextureIndexForFace(block, face);
       int atlasX = textureIndex % 16;  // 16 textures per row
       int atlasY = textureIndex / 16;
       
       float u1 = atlasX / 16.0f;
       float v1 = atlasY / 16.0f;
       float u2 = (atlasX + 1) / 16.0f;
       float v2 = (atlasY + 1) / 16.0f;
       
       meshBuilder.setUVRange(u1, v1, u2, v2);
   }
   
   private int getTextureIndexForFace(Block block, BlockFace face) {
       // Grass: top=0, side=1, bottom=2
       // Dirt: all faces=2
       // Stone: all faces=3
       // etc.
       return textureMapping.get(block.getId(), face);
   }
   ```

5. **Update Face Generation**
   ```java
   // In buildBlockFaces(), before each rect() call:
   if (world.getBlock(worldX, worldY + 1, worldZ) == air.getId()) {
       setUVForBlockFace(meshBuilder, block, BlockFace.TOP);
       meshBuilder.rect(...);  // Top face
   }
   ```

**Atlas Layout Example:**
```
Index | Block Type    | Face
------|--------------|-------
0     | Grass        | Top
1     | Grass        | Side
2     | Dirt         | All
3     | Stone        | All
4     | Cobblestone  | All
5     | Wood         | Top/Bottom
6     | Wood         | Side
7     | Leaves       | All
...   | ...          | ...
```

---

### Approach 2: Per-Block Individual Textures

**Description:**
Each block type has separate texture files. Materials are created per block type.

**Pros:**
✅ Simple to understand and implement
✅ Easy to swap individual textures
✅ No atlas management needed
✅ Quick prototyping

**Cons:**
❌ Poor performance (multiple texture bindings)
❌ Doesn't scale (100 blocks = 100 draw calls)
❌ High memory usage
❌ Not suitable for production

**Implementation:**
```java
public class LibGDXMaterialRepository implements BlockMaterialRepository {
    private final Map<Short, Material> materials = new HashMap<>();
    
    public LibGDXMaterialRepository() {
        Texture grass = new Texture(Gdx.files.internal("textures/grass.png"));
        materials.put((short)1, new Material(TextureAttribute.createDiffuse(grass)));
        
        Texture dirt = new Texture(Gdx.files.internal("textures/dirt.png"));
        materials.put((short)2, new Material(TextureAttribute.createDiffuse(dirt)));
        
        // ...for each block type
    }
    
    @Override
    public Material getMaterial(Block block) {
        return materials.get(block.getId());
    }
}
```

**Use Case:** Prototyping only, not recommended for production.

---

### Approach 3: Face-Aware Texture Mapping

**Description:**
Extension of Approach 1 that handles blocks with different textures per face (grass top vs side, log ends vs bark).

**Pros:**
✅ Realistic block rendering (like Minecraft)
✅ Supports complex blocks
✅ Still uses texture atlas (performant)

**Cons:**
❌ More complex material repository API
❌ Requires face direction in mesh generation
❌ More texture atlas slots needed

**Implementation:**
```java
public enum BlockFace { TOP, BOTTOM, NORTH, SOUTH, EAST, WEST }

public interface BlockMaterialRepository {
    Material getMaterial(Block block);
    int getTextureIndex(Block block, BlockFace face);  // NEW
}

public class LibGDXMaterialRepository implements BlockMaterialRepository {
    private Map<BlockFaceKey, Integer> textureIndices = new HashMap<>();
    
    public LibGDXMaterialRepository() {
        // Grass block
        textureIndices.put(new BlockFaceKey(1, BlockFace.TOP), 0);     // Grass texture
        textureIndices.put(new BlockFaceKey(1, BlockFace.BOTTOM), 2);  // Dirt texture
        textureIndices.put(new BlockFaceKey(1, BlockFace.NORTH), 1);   // Grass side
        textureIndices.put(new BlockFaceKey(1, BlockFace.SOUTH), 1);
        textureIndices.put(new BlockFaceKey(1, BlockFace.EAST), 1);
        textureIndices.put(new BlockFaceKey(1, BlockFace.WEST), 1);
        
        // Dirt block (same all sides)
        for (BlockFace face : BlockFace.values()) {
            textureIndices.put(new BlockFaceKey(2, face), 2);
        }
    }
    
    @Override
    public int getTextureIndex(Block block, BlockFace face) {
        return textureIndices.getOrDefault(new BlockFaceKey(block.getId(), face), 0);
    }
}
```

**Recommended:** Combine with Approach 1 for production implementation.

---

## Scene Integration Strategies

### Strategy 1: Hybrid Wrapper Approach ⭐ **SAFEST**

**Description:**
Keep ChunkMeshData extending GameMesh, add Scene wrapper for SceneManager rendering.

**Implementation:**
```java
public class ChunkMeshData extends GameMesh {
    private final btTriangleMesh triangle;
    private Scene sceneWrapper;  // NEW
    
    public ChunkMeshData(Model model, btTriangleMesh triangle, btBvhTriangleMeshShape shape) {
        super(model, shape);
        this.triangle = triangle;
        this.moving = false;
        
        // Create Scene wrapper for SceneManager rendering
        this.sceneWrapper = new Scene(new ModelInstance(model));
    }
    
    public Scene getScene() {
        return sceneWrapper;
    }
}
```

**ObjectRenderer Changes:**
```java
public void render(Float deltaTime) {
    // ... existing setup ...
    
    sceneManager.update(deltaTime);
    sceneManager.render();
    
    // Physics updates still use models list
    for (GameMesh obj : models) {
        if (obj.moving) {
            obj.transform.trn(0f, -delta, 0f);
            obj.body.setWorldTransform(obj.transform);
        }
    }
    
    colHandler.checkCollision();
    
    // Remove ModelBatch rendering entirely (after migration)
    // modelBatch.begin(camera);
    // for (ModelInstance obj : models) {
    //     modelBatch.render(obj, environment);
    // }
    // modelBatch.end();
}
```

**Pros:**
✅ Minimal code changes
✅ Physics system unchanged
✅ Can test incrementally
✅ Backwards compatible during migration

**Cons:**
❌ Temporary duplication (Scene + ModelInstance)
❌ Slight memory overhead

**Migration Steps:**
1. Add Scene wrapper to ChunkMeshData
2. Update ObjectRenderer to add chunks to SceneManager
3. Test rendering with both systems active
4. Disable ModelBatch rendering
5. Remove ModelBatch dependencies

---

### Strategy 2: Scene-First Architecture

**Description:**
Completely redesign ChunkMeshData to use Scene as primary rendering object.

**Implementation:**
```java
public class ChunkMeshData {
    private final Scene scene;
    private final btTriangleMesh triangle;
    private final btBvhTriangleMeshShape shape;
    private final btCollisionObject body;
    
    public ChunkMeshData(Model model, btTriangleMesh triangle, btBvhTriangleMeshShape shape) {
        this.scene = new Scene(new ModelInstance(model));
        this.triangle = triangle;
        this.shape = shape;
        
        this.body = new btCollisionObject();
        this.body.setCollisionShape(shape);
    }
    
    public Scene getScene() { return scene; }
    public btCollisionObject getBody() { return body; }
    
    public void dispose() {
        scene.dispose();
        body.dispose();
        shape.dispose();
        triangle.dispose();
    }
}
```

**Pros:**
✅ Clean architecture (no GameMesh inheritance)
✅ Single rendering system
✅ Better separation of concerns

**Cons:**
❌ Requires refactoring GameMesh hierarchy
❌ More changes across codebase
❌ Breaks existing abstractions

---

### Strategy 3: Composition Pattern (SOLID-Compliant) ⭐ **BEST LONG-TERM**

**Description:**
Separate rendering and physics concerns using composition and interfaces.

**Implementation:**

```java
// New interfaces
public interface IRenderable {
    Scene getScene();
    void setTransform(Matrix4 transform);
}

public interface ICollidable {
    btCollisionObject getBody();
    btCollisionShape getShape();
}

// Separate components
public class RenderableChunk implements IRenderable {
    private final Scene scene;
    
    public RenderableChunk(Model model) {
        this.scene = new Scene(new ModelInstance(model));
    }
    
    @Override
    public Scene getScene() { return scene; }
    
    @Override
    public void setTransform(Matrix4 transform) {
        scene.modelInstance.transform.set(transform);
    }
    
    public void dispose() {
        scene.dispose();
    }
}

public class PhysicsChunk implements ICollidable {
    private final btTriangleMesh triangle;
    private final btBvhTriangleMeshShape shape;
    private final btCollisionObject body;
    
    public PhysicsChunk(btTriangleMesh triangle, btBvhTriangleMeshShape shape) {
        this.triangle = triangle;
        this.shape = shape;
        this.body = new btCollisionObject();
        this.body.setCollisionShape(shape);
    }
    
    @Override
    public btCollisionObject getBody() { return body; }
    
    @Override
    public btCollisionShape getShape() { return shape; }
    
    public void dispose() {
        body.dispose();
        shape.dispose();
        triangle.dispose();
    }
}

// Refactored ChunkMeshData
public class ChunkMeshData implements IRenderable, ICollidable {
    private final RenderableChunk renderable;
    private final PhysicsChunk physics;
    
    public ChunkMeshData(Model model, btTriangleMesh triangle, btBvhTriangleMeshShape shape) {
        this.renderable = new RenderableChunk(model);
        this.physics = new PhysicsChunk(triangle, shape);
    }
    
    @Override
    public Scene getScene() { return renderable.getScene(); }
    
    @Override
    public void setTransform(Matrix4 transform) {
        renderable.setTransform(transform);
        physics.getBody().setWorldTransform(transform);
    }
    
    @Override
    public btCollisionObject getBody() { return physics.getBody(); }
    
    @Override
    public btCollisionShape getShape() { return physics.getShape(); }
    
    public void dispose() {
        renderable.dispose();
        physics.dispose();
    }
}
```

**ObjectRenderer Refactor:**
```java
public class ObjectRenderer {
    private SceneManager sceneManager;  // Only renderer needed
    private CollisionHandler colHandler;
    
    private List<IRenderable> renderables = new ArrayList<>();
    private List<ICollidable> collidables = new ArrayList<>();
    
    public void add(ChunkMeshData chunk) {
        renderables.add(chunk);
        collidables.add(chunk);
        sceneManager.addScene(chunk.getScene());
        colHandler.add(chunk);
    }
    
    public void render(Float deltaTime) {
        // ... setup ...
        
        // Only SceneManager rendering
        sceneManager.update(deltaTime);
        sceneManager.render();
        
        // Physics on collidables
        colHandler.checkCollision();
    }
}
```

**SOLID Principles Applied:**

1. **Single Responsibility Principle (SRP)**
   - RenderableChunk: Only handles rendering
   - PhysicsChunk: Only handles collision
   - ChunkMeshData: Coordinates both

2. **Open/Closed Principle (OCP)**
   - New renderable types can be added without modifying ObjectRenderer
   - New physics types can be added without modifying CollisionHandler

3. **Liskov Substitution Principle (LSP)**
   - Any IRenderable can replace another in ObjectRenderer
   - Any ICollidable can replace another in CollisionHandler

4. **Interface Segregation Principle (ISP)**
   - IRenderable and ICollidable are focused interfaces
   - Clients only depend on methods they use

5. **Dependency Inversion Principle (DIP)**
   - ObjectRenderer depends on IRenderable, not concrete classes
   - CollisionHandler depends on ICollidable, not concrete classes

**Pros:**
✅ Clean separation of concerns
✅ SOLID principles compliance
✅ Highly testable
✅ Flexible for future extensions
✅ Better maintainability

**Cons:**
❌ Most refactoring required
❌ Need to update multiple systems
❌ Temporary complexity during migration

---

## Clean Architecture Compliance

### Current Layer Analysis

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│  (ViewManager, GameView, Controls)  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Interface Adapters            │
│ (ObjectRenderer, ChunkLoader, etc)  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Use Cases Layer             │
│ (ChunkMeshGenerationInteractor)     │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Domain Layer                │
│    (Chunk, Block, World)            │
└─────────────────────────────────────┘
```

### Architectural Improvements

**Current Issues:**
- ObjectRenderer mixes rendering and physics concerns
- ChunkMeshData couples rendering (ModelInstance) with physics (Bullet)
- Domain entities shouldn't know about rendering details

**Recommended Structure:**

```
Domain Layer:
├── entities/
│   ├── Chunk.java (pure data, no rendering)
│   ├── Block.java (pure data)
│   └── World.java

Use Cases Layer:
├── ports/
│   ├── BlockMaterialRepository.java (interface)
│   ├── ChunkRenderer.java (interface) ← NEW
│   └── PhysicsCollider.java (interface) ← NEW
└── chunk_mesh_generation/
    └── ChunkMeshGenerationInteractor.java

Infrastructure Layer:
├── rendering/
│   ├── LibGDXMaterialRepository.java (implements BlockMaterialRepository)
│   ├── SceneBasedChunkRenderer.java (implements ChunkRenderer) ← NEW
│   └── TextureAtlasManager.java ← NEW
└── physics/
    └── BulletPhysicsCollider.java (implements PhysicsCollider) ← NEW
```

**Benefits:**
- Domain layer pure (no dependencies on frameworks)
- Use cases depend on interfaces (ports)
- Infrastructure implements interfaces (adapters)
- Easy to test (mock interfaces)
- Easy to swap implementations

---

## Recommended Implementation Roadmap

### Phase 1: Add Texture Support (Short Term)
**Goal:** Enable textured chunk rendering while maintaining current architecture

**Steps:**
1. Create texture atlas (256x256 with 16 block textures)
2. Add `Usage.TextureCoordinates` to vertex attributes
3. Update LibGDXMaterialRepository to use TextureAttribute
4. Implement UV coordinate calculation in ChunkMeshGenerationInteractor
5. Test with textured chunks rendered via ModelBatch

**Estimated Effort:** 2-3 days
**Risk:** Low (minimal changes to existing code)

### Phase 2: Hybrid Scene Integration (Medium Term)
**Goal:** Move chunk rendering to SceneManager

**Steps:**
1. Add Scene wrapper to ChunkMeshData (Strategy 1)
2. Update ObjectRenderer to add chunks to SceneManager
3. Test both rendering systems in parallel
4. Disable ModelBatch rendering
5. Remove ModelBatch dependencies

**Estimated Effort:** 1-2 weeks
**Risk:** Medium (requires careful testing of both systems)

### Phase 3: Architecture Refactor (Long Term)
**Goal:** Clean architecture with separated concerns

**Steps:**
1. Define IRenderable and ICollidable interfaces
2. Create RenderableChunk and PhysicsChunk components
3. Refactor ChunkMeshData to use composition (Strategy 3)
4. Update ObjectRenderer and CollisionHandler
5. Remove GameMesh inheritance from ChunkMeshData

**Estimated Effort:** 2-3 weeks
**Risk:** High (major refactoring, thorough testing needed)

### Phase 4: Optimization & Polish
**Goal:** Performance tuning and feature completion

**Steps:**
1. Implement face-aware texture mapping (grass, logs)
2. Add texture filtering options
3. Optimize chunk mesh generation
4. Add LOD (Level of Detail) support
5. Performance profiling and optimization

**Estimated Effort:** 1-2 weeks
**Risk:** Low (refinement of working system)

---

## Performance Considerations

### Texture Atlas Benefits
- **Draw Calls:** 1 per chunk (vs hundreds for individual textures)
- **Memory:** Single 256x256 texture ~256KB (vs ~1.6MB for 100 separate 16x16 textures)
- **GPU State Changes:** Minimal (one texture binding)

### Scene vs ModelBatch Performance
- **SceneManager:** Slightly more overhead but negligible for chunk count (<100)
- **Unified Pipeline:** Potential for shader optimizations across all objects
- **Batch Rendering:** Both support instancing if needed later

### Recommended Optimizations
1. **Greedy Meshing:** Combine adjacent faces with same texture (reduce vertices)
2. **Chunk Culling:** Don't render chunks outside view frustum
3. **LOD:** Use simplified meshes for distant chunks
4. **Async Loading:** Generate meshes on background thread (existing TODO)

---

## Testing Strategy

### Unit Tests
```java
@Test
public void testTextureAtlasUVCalculation() {
    // Test UV coords for each block type and face
    int textureIndex = 5;
    float[] uvs = calculateUVs(textureIndex, 16); // 16x16 atlas
    assertEquals(0.3125f, uvs[0], 0.001f); // u1
    assertEquals(0.0f, uvs[1], 0.001f);    // v1
    assertEquals(0.375f, uvs[2], 0.001f);  // u2
    assertEquals(0.0625f, uvs[3], 0.001f); // v2
}

@Test
public void testSceneWrapperCreation() {
    Model model = createTestModel();
    ChunkMeshData chunk = new ChunkMeshData(model, null, null);
    assertNotNull(chunk.getScene());
    assertEquals(model, chunk.getScene().modelInstance.model);
}
```

### Integration Tests
```java
@Test
public void testChunkRenderingWithTextures() {
    // Create chunk with blocks
    Chunk chunk = createTestChunk();
    
    // Generate mesh
    ChunkMeshData meshData = meshBuilder.buildModel(chunk);
    
    // Verify Scene creation
    Scene scene = meshData.getScene();
    assertNotNull(scene);
    
    // Verify texture material
    Material material = scene.modelInstance.materials.first();
    assertTrue(material.has(TextureAttribute.Diffuse));
}
```

### Visual Testing
1. Render test scene with textured chunks
2. Verify texture alignment (no seams between blocks)
3. Test different block types and face orientations
4. Check lighting interactions with textures
5. Performance profiling (FPS, draw calls, memory)

---

## File Structure for Texture Assets

```
assets/
└── textures/
    ├── blocks/
    │   ├── atlas_256.png          # Main texture atlas
    │   ├── atlas_256.atlas        # LibGDX atlas descriptor (optional)
    │   └── individual/            # Source textures (for atlas building)
    │       ├── grass_top.png
    │       ├── grass_side.png
    │       ├── dirt.png
    │       ├── stone.png
    │       └── ...
    └── atlas_mapping.json         # Block ID to texture index mapping
```

**atlas_mapping.json Example:**
```json
{
  "blocks": [
    {
      "id": 1,
      "name": "grass",
      "textures": {
        "top": 0,
        "bottom": 2,
        "sides": 1
      }
    },
    {
      "id": 2,
      "name": "dirt",
      "textures": {
        "all": 2
      }
    },
    {
      "id": 3,
      "name": "stone",
      "textures": {
        "all": 3
      }
    }
  ]
}
```

---

## Potential Challenges & Solutions

### Challenge 1: UV Coordinate Complexity
**Problem:** Calculating correct UVs for each face orientation
**Solution:** Create UV calculation utility class with unit tests

### Challenge 2: Texture Bleeding
**Problem:** Neighbor textures bleeding into block faces
**Solution:** 
- Add 1-2 pixel padding between atlas textures
- Use `TextureFilter.Nearest` for pixel-perfect rendering
- Ensure UV coords don't exceed texture boundaries

### Challenge 3: Physics Synchronization
**Problem:** Keeping rendering and physics transforms in sync
**Solution:** Use composition pattern (Strategy 3) with synchronized transform updates

### Challenge 4: Memory Management
**Problem:** Scene objects may use more memory than ModelInstance
**Solution:**
- Profile memory usage before/after migration
- Implement chunk unloading for distant chunks
- Consider object pooling for frequently created/destroyed chunks

### Challenge 5: Shader Compatibility
**Problem:** SceneManager default shaders may differ from ModelBatch
**Solution:**
- Test with both shader systems during hybrid phase
- Create custom shader if needed (consistent with DefaultShaderProvider)
- Ensure lighting calculations match between systems

---

## Conclusion

**Recommended Path Forward:**

1. **Immediate:** Implement Texture Atlas approach (Phase 1)
   - Low risk, high value
   - Enables textured voxel rendering
   - Compatible with current architecture

2. **Near-term:** Hybrid Scene Integration (Phase 2)
   - Consolidates rendering to SceneManager
   - Maintains stability with dual system during migration
   - Sets foundation for clean architecture

3. **Long-term:** Composition Pattern Refactor (Phase 3)
   - Achieves SOLID compliance
   - Separates rendering and physics concerns
   - Provides flexible, maintainable architecture

**Key Success Factors:**
- Incremental migration with testing at each phase
- Maintain backward compatibility during transitions
- Follow Clean Architecture and SOLID principles
- Comprehensive testing (unit, integration, visual)
- Performance monitoring throughout

**Expected Outcomes:**
- Unified rendering system (SceneManager only)
- Textured voxel chunks matching quality of GLTF models
- Clean separation between rendering and physics
- Maintainable, testable, extensible codebase
- Foundation for future enhancements (PBR materials, LOD, etc.)

---

**Document Version:** 1.0  
**Date:** 2025-11-21  
**Author:** Research Analysis  
**Status:** Complete
