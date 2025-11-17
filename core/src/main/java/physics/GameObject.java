package physics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

public class GameObject extends ModelInstance {

    public Model model;
    public btCollisionShape shape;

    public final btCollisionObject body;
    public boolean moving = true;
    public String id;
    public GameObject(Model model, String node, btCollisionShape shape){
        super(model, node);
        this.id = node;

        this.shape = shape;

        body = new btCollisionObject();
        body.setCollisionShape(shape);
    }

    public void dispose () {
        body.dispose();
        shape.dispose();
    }

//    static class Constructor implements Disposable {
//        public final Model model;
//        public final String node;
//        public final btCollisionShape shape;
//
//        public Constructor (Model model, String node, btCollisionShape shape) {
//            this.model = model;
//            this.node = node;
//            this.shape = shape;
//        }
//
//        public GameObject construct () {
//            return new GameObject(model, node, shape);
//        }
//
//        @Override
//        public void dispose () {
//            shape.dispose();
//        }
//    }

}
