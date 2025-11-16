package infrastructure.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

import physics.CollisionHandler;
import physics.GameMesh;

import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ObjectRenderer {
    public Environment environment;

    public PerspectiveCamera camera;
    public ModelBatch modelBatch;

    // RENDER LISTS & HANDLERS
    public List<GameMesh> models = new ArrayList<>(); // Changed from ModelInstance to GameMesh
    private SceneManager sceneManager;
    public CollisionHandler colHandler;

    // ASYNCHRONOUS QUEUES (Using GameMesh from Right/ModelInstance from Left implies GameMesh is correct)
    public BlockingQueue<GameMesh> toAdd = new LinkedBlockingQueue<>();
    public BlockingQueue<GameMesh> toRemove = new LinkedBlockingQueue<>(); // Added from Left for de-rendering

    public ObjectRenderer(PerspectiveCamera camera) {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();

        this.camera = camera;
        this.colHandler = colHandler;

        // Set up scene manager (from Right)
        sceneManager = new SceneManager();
        sceneManager.setShaderProvider(new DefaultShaderProvider());
        sceneManager.setCamera(camera);
        sceneManager.setAmbientLight(1f);
    }

    public void add(GameMesh obj) {
        toAdd.add(obj);
        colHandler.add(obj); // Add to collision handler (from Right)
    }

    public void remove(GameMesh obj) { // Added from Left, using GameMesh
        if (obj != null) {
            toRemove.add(obj);
        }
    }

    public void addToSceneManager(Scene scene) { // From Right
        System.out.println("Model added to scene.");
        sceneManager.addScene(scene);
    }


    private void updateRenderList() {
        GameMesh instance;

        // Add new models (from Middle/Right)
        while ((instance = toAdd.poll()) != null) {
            models.add(instance);
        }

        // Remove and dispose old models (from Left, adapted for GameMesh)
        while ((instance = toRemove.poll()) != null) {
            models.remove(instance);

            // Dispose of the model's resources when it's removed
            instance.dispose(); // Assuming GameMesh has a comprehensive dispose method
            instance.modelDispose(); // Use the modelDispose method from Right for Model resources

            // Also remove from collision handler if necessary (not explicitly in conflicts, but good practice)
            colHandler.remove(instance);
        }
    }

    public void render(Float deltaTime) {
        updateRenderList();

        // OpenGL State configuration (from Right)
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Render scene manager (from Right)
        sceneManager.update(deltaTime);
        sceneManager.render();

        // Movement Update (from Right)
        final float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
        for (GameMesh obj : models){
            if (obj.moving){
                obj.transform.trn(0f, -delta, 0f);
                obj.body.setWorldTransform(obj.transform);
            }
        }

        colHandler.checkCollision(); // Check collisions (from Right)

        modelBatch.begin(camera);
        // Render GameMesh objects (casting to ModelInstance is needed if GameMesh doesn't extend it)
        for (GameMesh obj : models) {
            modelBatch.render(obj, environment); // Render GameMesh
        }
        modelBatch.end();
    }

    public void dispose() {
        for (GameMesh obj: models){
            obj.dispose(); // Comprehensive GameMesh dispose
            obj.modelDispose(); // Dispose model resources (from Right)
        }
        models.clear();

        // Drain queues and dispose any remaining (from Left)
        updateRenderList();
        toAdd.clear();
        toRemove.clear();

        colHandler.dispose(); // Dispose collision handler (from Right)

        modelBatch.dispose();
        sceneManager.dispose(); // Dispose scene manager (from Right)
    }
}
