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

public class ObjectRenderer {
    public Environment environment;

    public PerspectiveCamera camera;
    public ModelBatch modelBatch;


    // Add scene manager (to load models)
    private SceneManager sceneManager;

    public BlockingQueue<GameMesh> toAdd = new LinkedBlockingQueue<>();

    public BlockingQueue<ModelInstance> toAdd = new LinkedBlockingQueue<>();
    public BlockingQueue<ModelInstance> toRemove = new LinkedBlockingQueue<>();
    public CollisionHandler colHandler;

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


    public void add(GameMesh obj){
        toAdd.add(obj);
        colHandler.add(obj);
    }

    public void addToSceneManager(Scene scene) { //To add model instances to the scene manager
        System.out.println("Zombie added to scene.");
        sceneManager.addScene(scene);
    }

    public void remove(ModelInstance modelInstance) {
        if (modelInstance != null) {
            toRemove.add(modelInstance);
        }
    }

    private void updateRenderList() {

        GameMesh instance;
        while ((instance = toAdd.poll()) != null){
        ModelInstance instance;

        // Add new models
        while ((instance = toAdd.poll()) != null) {
            models.add(instance);
        }


        // Remove and dispose old models
        while ((instance = toRemove.poll()) != null) {
            models.remove(instance);
            // Properly dispose of the model's resources when it's removed
            if (instance.model != null) {
                instance.model.dispose();
            }
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

        // Render scene manager
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
        // Dispose all models currently in the list
        for (ModelInstance modelInstance : models) {
            if (modelInstance.model != null) {
                modelInstance.model.dispose();
            }
        }
        models.clear();
        sceneManager.dispose();

        // Drain queues and dispose any remaining
        updateRenderList();
        toAdd.clear();
        toRemove.clear();

        modelBatch.dispose();
    }
}
