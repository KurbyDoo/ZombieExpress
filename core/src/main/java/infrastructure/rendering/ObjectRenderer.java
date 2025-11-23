package infrastructure.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import physics.CollisionHandler;
import physics.GameMesh;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ObjectRenderer {
    private SceneManager sceneManager;
    private MeshStorage meshStorage;

    public BlockingQueue<GameMesh> toAdd = new LinkedBlockingQueue<>();
    public BlockingQueue<GameMesh> toRemove = new LinkedBlockingQueue<>();
    public CollisionHandler colHandler;
    
    // Track all active meshes for physics sync
    private final List<GameMesh> activeMeshes = new ArrayList<>();

    public ObjectRenderer(PerspectiveCamera camera, CollisionHandler colHandler, MeshStorage meshStorage) {
        this.colHandler = colHandler;
        this.meshStorage = meshStorage;

        //set up scene manager
        sceneManager = new SceneManager();
        sceneManager.setCamera(camera);
        sceneManager.setAmbientLight(0.4f);
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
            activeMeshes.add(mesh); // Track for physics sync
        }

        // Remove and dispose old models
        while ((mesh = toRemove.poll()) != null) {
            sceneManager.removeScene(mesh.getScene());
            colHandler.remove(mesh.body); // Remove from collision world
            activeMeshes.remove(mesh); // Stop tracking
            // The instance is ChunkMeshData. It's dispose() method handles body/shape/triangle.
            mesh.dispose();
        }
    }


    public void render(Float deltaTime) {
        updateRenderList();

        // Physics simulation - must happen before rendering
        colHandler.dynamicsWorld.stepSimulation(deltaTime, 5, 1f/60f);

        // Sync physics transforms to scene transforms for dynamic objects
        // Check both activeMeshes (chunks, hitboxes) and meshStorage (entities)
        for (GameMesh mesh : activeMeshes) {
            if (mesh != null && !mesh.getIsStatic() && mesh.getScene() != null) {
                // Update the scene's model instance transform from the physics body's world transform
                mesh.body.getWorldTransform(mesh.getScene().modelInstance.transform);
            }
        }
        
        for (GameMesh mesh : meshStorage.getAllMeshes()) {
            if (mesh != null && !mesh.getIsStatic() && mesh.getScene() != null) {
                // Update the scene's model instance transform from the physics body's world transform
                mesh.body.getWorldTransform(mesh.getScene().modelInstance.transform);
            }
        }

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);

        Gdx.gl.glViewport(
            0,
            0,
            Gdx.graphics.getBackBufferWidth(),  // Use physical width
            Gdx.graphics.getBackBufferHeight() // Use physical height
        );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Render scene manager
        sceneManager.update(deltaTime);
        sceneManager.render();

//        modelBatch.begin(camera);

//        for (GameMesh obj : models) {
//            if (obj == null) continue;
//            obj.body.getWorldTransform(obj.getModelInstance().transform);
//            modelBatch.render(obj.getModelInstance(), environment);
//        }

//        for (GameMesh obj : meshStorage.getMeshes()) {
//            obj.body.getWorldTransform(obj.transform);
//            modelBatch.render(obj, environment);
//        }

//        modelBatch.end();
    }

    public void dispose() {
        // First, process any pending additions/removals to ensure they are moved to the 'models' list
        updateRenderList();

        // 1. Dispose all models currently in the 'models' list
//        for (GameMesh obj: models){
//            colHandler.remove(obj.body);
//            obj.dispose(); // Disposes body/shape/triangle

            // Critical: If it's ChunkMeshData, its unique LibGDX Model must also be disposed.
            // This catches any chunk that wasn't properly removed via the ChunkRenderer's logic.
//            if (obj instanceof ChunkMeshData) {
//                obj.modelDispose();
//            }
//        }
//        models.clear(); // Clear the list now that everything is disposed

        activeMeshes.clear(); // Clear tracked meshes
        colHandler.dispose();

        // ModelBatch and SceneManager must be disposed
//        modelBatch.dispose();
        sceneManager.dispose();

        // Queues should now be empty after updateRenderList, but we clear them defensively
        toAdd.clear();
        toRemove.clear();
    }
}
