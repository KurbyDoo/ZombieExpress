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
import com.badlogic.gdx.graphics.Color;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ObjectRenderer {
    public Environment environment;

    public PerspectiveCamera camera;
    public ModelBatch modelBatch;


    // Add scene manager (to load models)
    private SceneManager sceneManager;

    public BlockingQueue<GameMesh> toAdd = new LinkedBlockingQueue<>();
    public BlockingQueue<GameMesh> toRemove = new LinkedBlockingQueue<>();
    public CollisionHandler colHandler;

    public List<GameMesh> models = new ArrayList<>();

    public ObjectRenderer(PerspectiveCamera camera, CollisionHandler colHandler) {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // --- FOG COLOR (Simple) ---
        // Sets the color that distant objects fade into (light blue)
        Color skyColor = new Color(0.5f, 0.7f, 1.0f, 1f);
        environment.set(new ColorAttribute(ColorAttribute.Fog, skyColor));

        modelBatch = new ModelBatch();

        this.camera = camera;
        // fog gradient logiv
        this.camera.far = 100f;
        this.camera.near = 0.1f;
        this.camera.update();

        this.colHandler = colHandler;

        //set up scene manager
        sceneManager = new SceneManager();

        sceneManager.setShaderProvider(new PBRShaderProvider(new PBRShaderConfig()));

        sceneManager.setShaderProvider(new DefaultShaderProvider());
        sceneManager.setCamera(camera);
        sceneManager.setAmbientLight(1f);
    }


    public void add(GameMesh obj){
        toAdd.add(obj);
        colHandler.add(obj);
    }

    // Overloaded method for convenience when removing only a ModelInstance
    public void remove(ModelInstance modelInstance) {
        if (modelInstance instanceof GameMesh) {
            remove((GameMesh) modelInstance);
        } else if (modelInstance != null) {
            // For general ModelInstances without GameMesh/Collision:
            // This is kept empty as all chunk-related removals will be GameMesh/ChunkMeshData
        }
    }

    // Explicit method for removing a GameMesh
    public void remove(GameMesh obj) {
        if (obj != null) {
            toRemove.add(obj);
        }
    }

    public void addToSceneManager(Scene scene) { //To add model instances to the scene manager
//        System.out.println("Zombie added to scene.");
        sceneManager.addScene(scene);
    }

    public void removeFromSceneManager(Scene scene) {
        sceneManager.removeScene(scene);
    }

    private void updateRenderList() {

        GameMesh instance;
        // Add new models
        while ((instance = toAdd.poll()) != null) {
            models.add(instance);
        }


        // Remove and dispose old models
        while ((instance = toRemove.poll()) != null) {
            models.remove(instance);
            colHandler.remove(instance.body); // Remove from collision world
            // The instance is ChunkMeshData. It's dispose() method handles body/shape/triangle.
            instance.dispose();

            // NOTE: Chunk's unique Model disposal (modelDispose) is handled by ChunkRenderer
            // when it detects a chunk is removed or needs re-meshing.
        }
    }

//    public void addMeshData(ChunkMeshData data) {
//        meshData.add(data);
//    }

    public void render(Float deltaTime) {
        updateRenderList();

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);

        Gdx.gl.glViewport(
            0,
            0,
            Gdx.graphics.getBackBufferWidth(),  // Use physical width
            Gdx.graphics.getBackBufferHeight() // Use physical height
        );
        // --- SKY BACKGROUND ---
        // Sets the background color to light sky blue (0.5f, 0.7f, 1.0f)
        Gdx.gl.glClearColor(0.5f, 0.7f, 1.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Render scene manager
        sceneManager.update(deltaTime);
        sceneManager.render();

        // gravity
        colHandler.dynamicsWorld.stepSimulation(deltaTime, 5, 1f/60f);

        modelBatch.begin(camera);

        for (ModelInstance obj : models) {
            ((GameMesh)obj).body.getWorldTransform(obj.transform);
            modelBatch.render(obj, environment);
        }

        modelBatch.end();
    }

    public void dispose() {
        // First, process any pending additions/removals to ensure they are moved to the 'models' list
        updateRenderList();

        // 1. Dispose all models currently in the 'models' list
        for (GameMesh obj: models){
            colHandler.remove(obj.body);
            obj.dispose(); // Disposes body/shape/triangle

            // Critical: If it's ChunkMeshData, its unique LibGDX Model must also be disposed.
            // This catches any chunk that wasn't properly removed via the ChunkRenderer's logic.
            if (obj instanceof ChunkMeshData) {
                ((ChunkMeshData) obj).modelDispose();
            }
        }
        models.clear(); // Clear the list now that everything is disposed

        colHandler.dispose();

        // ModelBatch and SceneManager must be disposed
        modelBatch.dispose();
        sceneManager.dispose();

        // Queues should now be empty after updateRenderList, but we clear them defensively
        toAdd.clear();
        toRemove.clear();
    }
}
