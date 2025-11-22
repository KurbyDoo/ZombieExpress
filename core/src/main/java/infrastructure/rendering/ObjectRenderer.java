package infrastructure.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import physics.CollisionHandler;
import physics.GameMesh;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ObjectRenderer {
    public Environment environment;

    private SceneManager sceneManager;
    private CollisionHandler colHandler;

    private BlockingQueue<GameMesh> toAdd = new LinkedBlockingQueue<>();
    private BlockingQueue<GameMesh> toRemove = new LinkedBlockingQueue<>();

    public ObjectRenderer(PerspectiveCamera camera, CollisionHandler colHandler, MeshStorage meshStorage) {
        this.colHandler = colHandler;

        this.environment = new Environment();
        // Sky/Fog Color (Light Blue)
        Color skyColor = new Color(0.5f, 0.7f, 1.0f, 1f);
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        this.environment.set(new ColorAttribute(ColorAttribute.Fog, skyColor));
        DirectionalLight light = new DirectionalLight();
        light.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
        this.environment.add(light);

        //set up scene manager
        sceneManager = new SceneManager();
        sceneManager.setCamera(camera);
        sceneManager.setAmbientLight(0.1f);
        DirectionalLight light = new DirectionalLight();
        light.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
        sceneManager.environment.add(light);

        sceneManager.getRenderableProviders().add(meshStorage);
    }

    public void add(GameMesh mesh) {
        toAdd.add(mesh);
    }

    // Explicit method for removing a GameMesh
    public void remove(GameMesh mesh) {
        if (mesh != null) {
            toRemove.add(mesh);
        }
    }

    private void updateRenderList() {
        GameMesh mesh;
        // Add new models
        while ((mesh = toAdd.poll()) != null) {
            sceneManager.addScene(mesh.getScene());
            colHandler.add(mesh);
        }

        // Remove and dispose old models
        while ((mesh = toRemove.poll()) != null) {
            sceneManager.removeScene(mesh.getScene());
            colHandler.remove(mesh.getBody()); // Remove from collision world
            // The instance is ChunkMeshData. It's dispose() method handles body/shape/triangle.
            mesh.dispose();
        }
    }


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
    }

    public void dispose() {
        // First, process any pending additions/removals to ensure they are moved to the 'models' list
        updateRenderList();

        colHandler.dispose();
        sceneManager.dispose();

        toAdd.clear();
        toRemove.clear();
    }
}
