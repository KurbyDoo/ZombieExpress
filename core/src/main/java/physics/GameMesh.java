package physics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;
import infrastructure.rendering.ChunkMeshData;
import net.mgsx.gltf.scene3d.scene.Scene;

public class GameMesh {
//    public Model model;
    private Scene scene;
    private boolean isStatic;
    private btMotionState motionState;
    private ChunkMeshData chunkMeshData;

    public final btRigidBody body;
    public btCollisionShape shape;

    public int id;

    public GameMesh(int id, Scene scene, btRigidBody body) {
        this.scene = scene;
        this.id = id;
        this.body = body;
//        this.isStatic = true;

        // LINK TO AUTO UPDATE
        motionState = new btDefaultMotionState(scene.modelInstance.transform);
        this.body.setMotionState(motionState);
    }

    public void setChunkMeshData(ChunkMeshData chunkMeshData) {
        this.chunkMeshData = chunkMeshData;
    }

    public void dispose () {
        body.dispose();
        if (motionState != null) {
            motionState.dispose();
        }
        if (chunkMeshData != null) {
            chunkMeshData.dispose();
        }
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    public boolean getIsStatic() {
        return isStatic;
    }

//    public ModelInstance getModelInstance() {
//        return modelInstance;
//    }

    /**
     * Might be needed since model is disposed at the very end separately from the body & shape.
     */
//    public void modelDispose(){
//        model.dispose();
//    }
}
