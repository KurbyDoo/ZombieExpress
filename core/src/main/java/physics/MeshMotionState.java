package physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import domain.GamePosition;
import domain.entities.Entity;

public class MeshMotionState extends btMotionState {
    private final Matrix4 visualTransform;
    private Matrix4 hitboxTransform;
    private final Vector3 offset;
    private final Entity entity;

    private final Vector3 tempPos = new Vector3();

    public MeshMotionState(Matrix4 visualTransform, Vector3 offset, Entity entity) {
        this.visualTransform = visualTransform;
        this.offset = offset;
        this.entity = entity;
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

        worldTrans.getTranslation(tempPos);
        tempPos.add(offset);
        entity.setPosition(new GamePosition(tempPos.x, tempPos.y, tempPos.z));
    }
}
