package application.use_cases.ports;

import com.badlogic.gdx.math.Vector3;

public interface PhysicsControlPort {
    void setLinearVelocity(int entityId, float x, float y, float z);
    Vector3 getLinearVelocity(int entityId); // Needed to preserve falling speed
    void lookAt(int entityId, Vector3 targetPosition);
}
