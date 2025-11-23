package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import com.badlogic.gdx.utils.Disposable;

public class ChunkMeshData implements Disposable {
    public final Model model;

    public final btBvhTriangleMeshShape shape;
    public final btTriangleMesh triangleMesh;

    public ChunkMeshData(Model model, btBvhTriangleMeshShape shape, btTriangleMesh triangleMesh) {
        this.model = model;
        this.shape = shape;
        this.triangleMesh = triangleMesh;
    }

    @Override
    public void dispose() {
        model.dispose();
        shape.dispose();
        triangleMesh.dispose();
    }
}
