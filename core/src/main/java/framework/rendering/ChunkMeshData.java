package framework.rendering;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import com.badlogic.gdx.utils.Disposable;

public class ChunkMeshData implements Disposable {
    private final Model model;
    private final btBvhTriangleMeshShape shape;
    private final btTriangleMesh triangleMesh;

    public ChunkMeshData(Model model, btBvhTriangleMeshShape shape, btTriangleMesh triangleMesh) {
        this.model = model;
        this.shape = shape;
        this.triangleMesh = triangleMesh;
    }

    public btBvhTriangleMeshShape getShape() {
        return shape;
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void dispose() {
        model.dispose();
        shape.dispose();
        triangleMesh.dispose();
    }
}
