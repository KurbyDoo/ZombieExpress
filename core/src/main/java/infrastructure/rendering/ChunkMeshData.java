package infrastructure.rendering;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import physics.GameObject;

public class ChunkMeshData {
    final private Model model;
    final private btTriangleMesh triangle;
    final private btBvhTriangleMeshShape shape;
    final private GameObject object;
//    public GameObject gameObject = null;
//    public ChunkMeshData(){
//        model = null;
//        triangle = null;
//        shape = null;
//    }

    public ChunkMeshData(Model model, btTriangleMesh triangle, btBvhTriangleMeshShape shape){
        assert model != null;
        this.model = model;
        this.triangle = triangle;
        this.shape = shape;
        this.object = new GameObject(this.model, "BvhTriangleMesh", this.shape);

    }

    /**
     * WE MUST DISPOSE WHEN DE-RENDERING UNLOADING A CHUNK
     */
    public void dispose() {
        model.dispose();
        triangle.dispose();
        shape.dispose();
    }

    public GameObject getGameObject() {
        return this.object;
    }

    public btTriangleMesh getTriangleMesh() {
        return this.triangle;
    }

    public btBvhTriangleMeshShape getBvhTriangle() {
        return this.shape;
    }

}
