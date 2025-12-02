package physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import infrastructure.rendering.ChunkMeshData;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.scene.Scene;

public class GameMesh {
    private final Scene scene;
    private boolean isStatic;
    private final btMotionState motionState;

    private ModelInstance hitboxInstance;
    private Model hitboxModel;
    private boolean showHitbox = false;
    private final btRigidBody body;
    private ChunkMeshData chunkMeshData;

    public int id;

    public GameMesh(int id, Scene scene, btRigidBody body, btMotionState motionState) {
        this.scene = scene;
        this.id = id;
        this.body = body;
        this.motionState = motionState;

        createHitboxFromScene(scene);
    }

    private void createHitboxFromScene(Scene targetScene) {
        BoundingBox bbox = new BoundingBox();

        try {
            targetScene.modelInstance.calculateBoundingBox(bbox);
        } catch (Exception e) {
            // Log warning but don't crash. This mesh just won't have a debug hitbox.
            Gdx.app.debug("GameMesh", "Skipping hitbox for ID " + id + ": Mesh contains empty parts/invalid geometry.");
            return;
        }

        Vector3 fullDimensions = new Vector3();
        bbox.getDimensions(fullDimensions);

        // If the dimension is zero (e.g. a single point or empty mesh), skip hitbox
        if (fullDimensions.isZero()) {
            return;
        }
        Material mat = new Material();
        mat.set(PBRColorAttribute.createBaseColorFactor(new Color(0f, 0f, 1f, 0.4f)));
        mat.set(new BlendingAttribute(true, 0.4f));

        // TODO: USE MODEL REPO
        ModelBuilder modelBuilder = new ModelBuilder();
        this.hitboxModel = modelBuilder.createBox(
            fullDimensions.x, fullDimensions.y, fullDimensions.z,
            mat,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        this.hitboxInstance = new ModelInstance(hitboxModel);
//        this.hitboxInstance.transform = targetScene.modelInstance.transform;
        this.hitboxInstance.transform = new Matrix4();
        body.getWorldTransform(this.hitboxInstance.transform);
        if (this.motionState instanceof MeshMotionState) {
            ((MeshMotionState) this.motionState).setHitboxTransform(this.hitboxInstance.transform);
        }
    }

    public void setShowHitbox(boolean show) {
        this.showHitbox = show;
    }

    public boolean isShowHitbox() {
        return showHitbox;
    }

    public ModelInstance getHitboxInstance() {
        return hitboxInstance;
    }


    public void setChunkMeshData(ChunkMeshData chunkMeshData) {
        this.chunkMeshData = chunkMeshData;
    }


    public void dispose () {
        body.dispose();
        if (motionState != null) {
            motionState.dispose();
        }
        if (hitboxModel != null) {
            hitboxModel.dispose();
        }
        if (chunkMeshData != null) {
            chunkMeshData.dispose();
        }
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public btRigidBody getBody() {
        return body;
    }

    public Scene getScene() {
        return scene;
    }

    public boolean getIsStatic() {
        return isStatic;
    }
}
