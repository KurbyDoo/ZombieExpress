package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import physics.GameMesh;

public class ChunkMeshData extends GameMesh {

    final private btTriangleMesh triangle;

    public ChunkMeshData(Model model, btTriangleMesh triangle, btBvhTriangleMeshShape shape, float mass){
        super(model, shape, mass);
        this.triangle = triangle;
        this.moving = false; // delete later
    }

    /**
     * WE MUST DISPOSE WHEN DE-RENDERING UNLOADING A CHUNK
     */
    public void dispose() {
        super.dispose();
        triangle.dispose();
    }
}
