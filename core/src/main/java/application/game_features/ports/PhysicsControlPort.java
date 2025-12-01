package application.game_features.ports;

import domain.GamePosition;

public interface PhysicsControlPort {
    void setLinearVelocity(int entityId, float x, float y, float z);
    GamePosition getLinearVelocity(int entityId); // Needed to preserve falling speed
    void lookAt(int entityId, GamePosition targetPosition);
}
