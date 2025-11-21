package infrastructure.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import physics.CollisionHandler;
import physics.GameMesh;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ARCHITECTURAL ISSUE #1: Mixed Rendering Systems
 * 
 * This class currently manages TWO separate rendering pipelines:
 * 1. ModelBatch for GameMesh/ModelInstance objects (used for chunks and collision objects)
 * 2. SceneManager for Scene objects (used for entities loaded from .gltf files)
 * 
 * PROBLEM: Transparency rendering (alpha < 1.0) only works correctly in SceneManager.
 * When a Scene object has transparent surfaces, it correctly renders objects behind it.
 * However, GameMesh objects (chunks) are NOT visible behind transparent Scene objects
 * because they are rendered in a separate pipeline (ModelBatch).
 * 
 * CLEAN ARCHITECTURE VIOLATION:
 * - This class depends on physics.CollisionHandler (domain/application layer concern)
 * - Infrastructure layer should not directly couple with collision detection
 * - Violates Dependency Inversion Principle
 * 
 * RECOMMENDATION FOR CONSOLIDATION:
 * 1. Unify both rendering pipelines to use SceneManager exclusively
 *    - SceneManager has better support for transparency and modern rendering
 *    - Can handle both procedurally generated meshes and loaded models
 * 
 * 2. Separate collision management from rendering:
 *    - Remove CollisionHandler dependency from this class
 *    - Create an "updater" layer that synchronizes entity positions with both
 *      rendering meshes and collision meshes independently
 * 
 * 3. Create a unified RenderableEntity interface:
 *    - All entities (chunks, zombies, player, etc.) implement this interface
 *    - ObjectRenderer only knows about RenderableEntity, not specific types
 *    - Supports both procedural geometry and loaded models
 */
public class ObjectRenderer {
    public Environment environment;

    public PerspectiveCamera camera;
    
    // ARCHITECTURAL NOTE: ModelBatch is for rendering ModelInstance objects (chunks, procedural geometry)
    public ModelBatch modelBatch;


    // ARCHITECTURAL NOTE: SceneManager is for rendering Scene objects (loaded .gltf models)
    // SceneManager handles transparency correctly, ModelBatch does not always handle depth sorting properly
    private SceneManager sceneManager;

    // ARCHITECTURAL ISSUE: Using GameMesh (which couples rendering + collision) instead of pure rendering data
    // RECOMMENDATION: Replace with a pure rendering data structure that doesn't know about collision
    public BlockingQueue<GameMesh> toAdd = new LinkedBlockingQueue<>();

    // CLEAN ARCHITECTURE VIOLATION: Rendering layer should not manage collision directly
    // RECOMMENDATION: Move collision handling to a separate CollisionSystem that runs in parallel
    public CollisionHandler colHandler;

    // ARCHITECTURAL ISSUE: Stores GameMesh objects which mix rendering and collision concerns
    // RECOMMENDATION: Separate into two lists - one for rendering, one for collision bodies
    public List<GameMesh> models = new ArrayList<>();

    public ObjectRenderer(PerspectiveCamera camera, CollisionHandler colHandler) {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();

        this.camera = camera;

        this.colHandler = colHandler;

        //set up scene manager
        sceneManager = new SceneManager();
        sceneManager.setShaderProvider(new DefaultShaderProvider());
        sceneManager.setCamera(camera);
        sceneManager.setAmbientLight(1f);
    }


    // ARCHITECTURAL ISSUE: This method couples rendering with collision
    // When adding a visual mesh, it automatically adds to collision system
    // RECOMMENDATION: Separate these concerns - entities should have independent render and collision meshes
    public void add(GameMesh obj){
        toAdd.add(obj);
        colHandler.add(obj);  // Collision should be managed separately
    }

    // ARCHITECTURAL NOTE: This is the correct approach for entities - separate method for scene-based rendering
    // RECOMMENDATION: All entities should use this pathway (SceneManager) for proper transparency support
    public void addToSceneManager(Scene scene) { //To add model instances to the scene manager
        System.out.println("Zombie added to scene.");
        sceneManager.addScene(scene);
    }

    private void updateRenderList() {

        GameMesh instance;
        while ((instance = toAdd.poll()) != null){
            models.add(instance);
        }

    }

//    public void addMeshData(ChunkMeshData data) {
//        meshData.add(data);
//    }

    public void render(Float deltaTime) {
        updateRenderList();

        // GPT said to implement these idk what they do
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // ARCHITECTURAL ISSUE: SceneManager renders FIRST
        // This is why Scene objects appear correctly with transparency
        sceneManager.update(deltaTime);
        sceneManager.render();

        // ARCHITECTURAL ISSUE: Movement logic in renderer violates Single Responsibility Principle
        // RECOMMENDATION: Move this to an EntityUpdater or MovementSystem
        // The renderer should only render, not update entity positions
        final float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
        for (GameMesh obj : models){
            if (obj.moving){
                obj.transform.trn(0f, -delta, 0f);
                obj.body.setWorldTransform(obj.transform);
            }
        }

        // ARCHITECTURAL ISSUE: Collision detection in render method violates SRP
        // RECOMMENDATION: Move collision checking to a separate CollisionSystem that runs independently
        colHandler.checkCollision();

        // ARCHITECTURAL ISSUE: ModelBatch renders SECOND, AFTER SceneManager
        // This is the ROOT CAUSE of the transparency problem:
        // - Transparent Scene objects write to depth buffer
        // - ModelBatch objects are then depth-tested against Scene objects
        // - ModelBatch objects behind transparent Scene objects fail depth test and are not rendered
        // 
        // SOLUTION: Either:
        // 1. Render all opaque objects first (ModelBatch + SceneManager), then all transparent objects
        // 2. Convert everything to use SceneManager exclusively for unified depth sorting
        modelBatch.begin(camera);

        for (ModelInstance obj : models) {
            modelBatch.render(obj, environment);
        }

        modelBatch.end();
    }

    public void dispose() {

        for (GameMesh obj: models){
            obj.dispose();
        }

        colHandler.dispose();

        modelBatch.dispose();
        for (GameMesh obj : models){
            obj.modelDispose();
        }
        models.clear();
        sceneManager.dispose();
    }
}
