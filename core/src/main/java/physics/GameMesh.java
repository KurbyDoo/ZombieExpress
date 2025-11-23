package physics;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import infrastructure.rendering.ChunkMeshData;
import net.mgsx.gltf.scene3d.scene.Scene;

public class GameMesh {
    private Scene scene;
    private boolean isStatic;
    private btMotionState motionState;

    public final btRigidBody body;
    private ChunkMeshData chunkMeshData;

    public int id;

    public GameMesh(int id, Scene scene, btRigidBody body) {
        this.scene = scene;
        this.id = id;
        this.body = body;
        isStatic = false;

        // LINK TO AUTO UPDATE
//        motionState = new btDefaultMotionState(scene.modelInstance.transform);
//        this.body.setMotionState(motionState);
        motionState = body.getMotionState();
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
}
