package physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class MeshMotionState extends btMotionState {
    private final Matrix4 visualTransform;
    private Matrix4 hitboxTransform;
    private final Vector3 offset;

    public MeshMotionState(Matrix4 visualTransform, Vector3 offset) {
        this.visualTransform = visualTransform;
        this.offset = offset;
    }

    public void setHitboxTransform(Matrix4 hitboxTransform) {
        this.hitboxTransform = hitboxTransform;
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(visualTransform);
        worldTrans.translate(0, -offset.y, 0);
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        if (hitboxTransform != null) {
            hitboxTransform.set(worldTrans);
        }

        visualTransform.set(worldTrans);
        visualTransform.translate(offset);
    }
}
