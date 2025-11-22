package physics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class GameMesh extends ModelInstance {

    public Model model;

    public final btRigidBody body;
    public boolean moving = true;

    private final Vector3 localInertia = new Vector3();
    public  btCollisionShape shape;
    public float mass;


    public static int COUNTER;
    public int id;

    public GameMesh(Model model, btCollisionShape shape, float mass){
        super(model);
        this.model = model;
        this.shape = shape;
        this.mass = mass;
        COUNTER++;
        id += COUNTER;

        if (mass > 0f)
            shape.calculateLocalInertia(mass, localInertia);
        else
            localInertia.set(0, 0, 0);

        this.body = new btRigidBody(mass, null, shape, localInertia);
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
