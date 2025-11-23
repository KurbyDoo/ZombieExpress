package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.utils.Disposable;

public class ChunkMeshData implements Disposable {
    public final Model model;

    public final btBvhTriangleMeshShape shape;

    public ChunkMeshData(Model model, btBvhTriangleMeshShape shape) {
        this.model = model;
        this.shape = shape;
    }

    @Override
    public void dispose() {
        model.dispose();
        shape.dispose();
    }
}
