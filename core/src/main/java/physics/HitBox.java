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
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
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
    private final Material material;

    public int w;
    public int h;
    public int d;
    public String id;
    public ShapeTypes type;

    /**
     * Helper to create a matte (non-shiny) PBR material.
     */
    private Material createPBRMaterial(Color color) {
        Material mat = new Material();

        // 1. The Color (Albedo) - Critical for PBR
        mat.set(PBRColorAttribute.createBaseColorFactor(color));

        // 2. Metallic (0 = dielectric/plastic/wood/dirt, 1 = metal)
        mat.set(PBRFloatAttribute.createMetallic(0.0f));

        // 3. Roughness (0 = smooth mirror, 1 = matte/rough)
        // Set to 1.0f for dirt/stone so it doesn't look like wet plastic
        mat.set(PBRFloatAttribute.createRoughness(1.0f));

        return mat;
    }

    public HitBox(String id, ShapeTypes type, int w, int h, int d){
        this.id = id;
        this.type = type;
        this.w = w;
        this.h = h;
        this.d = d;
        material = createPBRMaterial(TRANSPARENT);
    }

    // TODO: Move into entity factory and fix gravity
    public GameMesh construct() {
        Model model;
        btCollisionShape shape;

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

        ModelInstance instance = new ModelInstance(model);
        instance.transform.setToTranslation(new Vector3(20f, 100f, 90f));
        Scene scene = new Scene(instance);

        float mass = 1f; // Dynamic object
        Vector3 localInertia = new Vector3();

        shape.calculateLocalInertia(mass, localInertia);
        btMotionState motionState = new btDefaultMotionState(instance.transform);

        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, localInertia);
        btRigidBody body = new btRigidBody(info);
        info.dispose();

        int numericId = id.hashCode();

        return new GameMesh(numericId, scene, body, motionState);
    }

    @Override
    public void dispose(){
        if (lastCreatedModel != null) {
            lastCreatedModel.dispose();
        }
    }
}
