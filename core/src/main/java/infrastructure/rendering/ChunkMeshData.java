package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import physics.GameObject;

public class ChunkMeshData extends GameObject{

    final private btTriangleMesh triangle;

    public ChunkMeshData(Model model, String node, btTriangleMesh triangle, btBvhTriangleMeshShape shape){
        super(model, node, shape);
        this.triangle = triangle;
    }

    /**
     * WE MUST DISPOSE WHEN DE-RENDERING UNLOADING A CHUNK
     */
    public void dispose() {
        super.dispose();
        triangle.dispose();
        model.dispose();
    }
}
