package physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.scene3d.scene.Scene;

import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;

/**
 * Placeholder to build falling debug shapes.
 */
public class HitBox implements Disposable {

    public enum ShapeTypes{
        SPHERE,
        BOX
    }

    private final ModelBuilder modelBuilder = new ModelBuilder();

    // We keep track of the last created model to dispose it if needed,
    // though ideally a Factory should manage assets better.
    private Model lastCreatedModel;

    // transparent red material
    static private final Color TRANSPARENT = new Color(1f, 0f, 0f, 0.5f);
    static private final BlendingAttribute ATTRIBUTE = new BlendingAttribute(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    static private final Material material = new Material(ColorAttribute.createDiffuse(TRANSPARENT), ATTRIBUTE);

    public int w;
    public int h;
    public int d;
    public String id;
    public ShapeTypes type;

    public HitBox(String id, ShapeTypes type, int w, int h, int d){
        this.id = id;
        this.type = type;
        this.w = w;
        this.h = h;
        this.d = d;
    }

    public GameMesh construct() {
        Model model;
        btCollisionShape shape;

        // 1. Generate Model & Shape
        switch(type){
            case BOX:
                model = modelBuilder.createBox(w, h, d, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                shape = new btBoxShape(new Vector3(w/2f, h/2f, d/2f));
                break;

            case SPHERE:
                float radius = Math.max(w, Math.max(h, d)) / 2f;
                model = modelBuilder.createSphere(w, h, d, 8, 8, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                shape = new btSphereShape(radius);
                break;
            default:
                throw new IllegalArgumentException("Unknown Shape type: " + type);
        }

        this.lastCreatedModel = model;

        // 2. Create Visual Scene (Wrapper for ModelInstance)
        ModelInstance instance = new ModelInstance(model);
        Scene scene = new Scene(instance);

        // 3. Setup Physics Logic
        float mass = 1f; // Dynamic object
        Vector3 localInertia = new Vector3();

        // Important: Calculate inertia so the object spins/falls correctly
        shape.calculateLocalInertia(mass, localInertia);

        // 4. Create Motion State
        // This syncs the Physics position -> Graphics position automatically
        btMotionState motionState = new btDefaultMotionState(instance.transform);

        // 5. Create Body
        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
        btRigidBody body = new btRigidBody(info);
        info.dispose(); // Dispose info immediately after creation

        // 6. Convert String ID to Int (GameMesh uses Int ID)
        int numericId = id.hashCode();

        // 7. Return the new Composition-based GameMesh
        return new GameMesh(numericId, scene, body);
    }

    @Override
    public void dispose(){
        if (lastCreatedModel != null) {
            lastCreatedModel.dispose();
        }
    }
}
