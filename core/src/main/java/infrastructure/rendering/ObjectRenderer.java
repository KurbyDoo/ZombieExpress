/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Infrastructure/Rendering)
 *
 * DESIGN PATTERNS:
 * - Facade Pattern: Provides simplified rendering interface.
 * - Queue Pattern: Uses BlockingQueues for thread-safe mesh management.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] This is the correct layer for LibGDX rendering code.
 * - [PASS] Framework dependencies (LibGDX) are appropriate.
 * - [PASS] Encapsulates all rendering details.
 *
 * SOLID PRINCIPLES:
 * - [WARN] SRP: Manages rendering, environment, AND mesh queues.
 *   Consider splitting into separate concerns.
 * - [PASS] DIP: Depends on MeshStorage abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Public field 'environment' should be private with getter.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Public field violates encapsulation.
 * - [MINOR] Missing class-level Javadoc.
 */
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
    private final SceneManager sceneManager;
    private final CollisionHandler colHandler;
    private final PerspectiveCamera camera;

    private BlockingQueue<GameMesh> toAdd = new LinkedBlockingQueue<>();
    private BlockingQueue<GameMesh> toRemove = new LinkedBlockingQueue<>();

    public ObjectRenderer(PerspectiveCamera camera, CollisionHandler colHandler, MeshStorage meshStorage) {
        this.colHandler = colHandler;
        this.camera = camera;

        //set up scene manager
        // TODO: Should this be dependency injected?
        sceneManager = new SceneManager();
        sceneManager.setCamera(camera);
        sceneManager.setAmbientLight(0.1f);
        DirectionalLight light = new DirectionalLight();
        light.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
        sceneManager.environment.add(light);

        this.environment = sceneManager.environment;
        // Color skyColor = new Color(0.5f, 0.7f, 1.0f, 1f); // blue
        // Color skyColor = new Color(0.5f, 0.55f, 0.65f, 1f); // grey
        Color skyColor = new Color(0.1f, 0.15f, 0.25f, 1f); // dark blue
        this.environment.set(new ColorAttribute(ColorAttribute.Fog, skyColor));

        this.camera.far = 100f;
        this.camera.near = 0.1f;
        this.camera.update();

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
        // Set the background color to fog colour
        Color fogColor = ((ColorAttribute)this.environment.get(ColorAttribute.Fog)).color;
        Gdx.gl.glClearColor(fogColor.r, fogColor.g, fogColor.b, fogColor.a);
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
