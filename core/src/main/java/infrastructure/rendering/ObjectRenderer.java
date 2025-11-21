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
 * RESEARCH NOTE: DUAL RENDERING SYSTEM ARCHITECTURE
 * ================================================
 * This class currently manages TWO separate rendering pipelines:
 * 
 * 1. MODEL INSTANCE RENDERING (for chunks):
 *    - Uses: ModelBatch + ModelInstance
 *    - Purpose: Renders voxel chunk meshes with SOLID COLOR materials
 *    - Data flow: Chunk -> ChunkMeshGenerationInteractor -> ChunkMeshData (extends GameMesh) -> ModelBatch
 *    - Materials: LibGDXMaterialRepository provides ColorAttribute-based materials
 *    - Limitation: NO TEXTURE SUPPORT - only solid colors per block type
 * 
 * 2. SCENE RENDERING (for textured models):
 *    - Uses: SceneManager + Scene
 *    - Purpose: Renders GLTF models (zombies, decorations) with FULL TEXTURE SUPPORT
 *    - Data flow: GLTF file -> SceneAsset -> Scene -> SceneManager
 *    - Materials: Embedded in GLTF files with texture references
 *    - Benefit: Full PBR materials, textures, and complex model support
 * 
 * CONSOLIDATION GOAL:
 * Convert chunk rendering to use Scene objects instead of ModelInstance so both
 * systems use SceneManager, enabling texture mapping on voxel chunks.
 */
public class ObjectRenderer {
    public Environment environment;

    public PerspectiveCamera camera;
    public ModelBatch modelBatch;


    // Add scene manager (to load models)
    private SceneManager sceneManager;

    public BlockingQueue<GameMesh> toAdd = new LinkedBlockingQueue<>();

    public CollisionHandler colHandler;

    public List<GameMesh> models = new ArrayList<>();

    public ObjectRenderer(PerspectiveCamera camera, CollisionHandler colHandler) {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // RESEARCH NOTE: ModelBatch is used for chunk rendering (ModelInstance-based)
        // CONSOLIDATION: After migration, this could be removed if all rendering uses SceneManager
        modelBatch = new ModelBatch();

        this.camera = camera;

        this.colHandler = colHandler;

        // RESEARCH NOTE: SceneManager handles GLTF-based Scene objects
        // CONSOLIDATION TARGET: This should become the ONLY rendering system
        // It already supports the environment lighting needed for both chunks and models
        sceneManager = new SceneManager();
        sceneManager.setShaderProvider(new DefaultShaderProvider());
        sceneManager.setCamera(camera);
        sceneManager.setAmbientLight(1f);
    }


    public void add(GameMesh obj){
        toAdd.add(obj);
        colHandler.add(obj);
    }

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

        // RESEARCH NOTE: RENDERING PIPELINE SPLIT
        // ========================================
        // Currently TWO separate render calls:
        
        // 1. Render textured models (zombies) via SceneManager
        sceneManager.update(deltaTime);
        sceneManager.render();

        // Movement Update
        final float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
        for (GameMesh obj : models){
            if (obj.moving){
                obj.transform.trn(0f, -delta, 0f);
                obj.body.setWorldTransform(obj.transform);
            }
        }

        colHandler.checkCollision();

        // 2. Render chunks via ModelBatch (solid color materials only)
        // CONSOLIDATION: This entire section should be replaced with SceneManager rendering
        // after chunks are converted to Scene objects with texture support
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
