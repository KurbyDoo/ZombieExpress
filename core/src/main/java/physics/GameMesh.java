package physics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class GameMesh extends ModelInstance {

    public Model model;
    public btCollisionShape shape;

    public final btCollisionObject body;
    public boolean moving = true;

    public static int COUNTER;
    public int id;

    public GameMesh(Model model, btCollisionShape shape){
        super(model);
        this.model = model;
        this.shape = shape;

        COUNTER++;
        id += COUNTER;

        body = new btCollisionObject();
        body.setCollisionShape(shape);


    }

    public void dispose () {
        body.dispose();
        shape.dispose();
    }

    /**
     * Might be needed since model is disposed at the very end separately from the body & shape.
     */
    public void modelDispose(){
        model.dispose();
    }
}
