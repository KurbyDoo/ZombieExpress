package application.use_cases.update_entity;

import application.use_cases.ports.PhysicsControlPort;
import domain.GamePosition;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of PhysicsControlPort for unit testing.
 * Records all physics operations for verification.
 */
public class StubPhysicsControlPort implements PhysicsControlPort {

    private final Map<Integer, GamePosition> velocities = new HashMap<>();
    private final Map<Integer, GamePosition> lookAtTargets = new HashMap<>();

    // Track calls for verification
    private int setVelocityCalls = 0;
    private int getVelocityCalls = 0;
    private int lookAtCalls = 0;

    // Configuration for testing
    private boolean returnNullVelocity = false;

    @Override
    public void setLinearVelocity(int entityId, float x, float y, float z) {
        velocities.put(entityId, new GamePosition(x, y, z));
        setVelocityCalls++;
    }

    @Override
    public GamePosition getLinearVelocity(int entityId) {
        getVelocityCalls++;
        if (returnNullVelocity) {
            return null;
        }
        return velocities.getOrDefault(entityId, new GamePosition(0, 0, 0));
    }

    @Override
    public void lookAt(int entityId, GamePosition targetPosition) {
        lookAtTargets.put(entityId, new GamePosition(targetPosition));
        lookAtCalls++;
    }

    // Test helper methods

    public GamePosition getLastVelocity(int entityId) {
        return velocities.get(entityId);
    }

    public GamePosition getLastLookAtTarget(int entityId) {
        return lookAtTargets.get(entityId);
    }

    public int getSetVelocityCalls() {
        return setVelocityCalls;
    }

    public int getGetVelocityCalls() {
        return getVelocityCalls;
    }

    public int getLookAtCalls() {
        return lookAtCalls;
    }

    public void setInitialVelocity(int entityId, GamePosition velocity) {
        velocities.put(entityId, velocity);
    }

    /**
     * Configure the stub to return null for all getLinearVelocity calls.
     * Useful for testing null handling.
     */
    public void setReturnNullVelocity(boolean returnNull) {
        this.returnNullVelocity = returnNull;
    }

    public void reset() {
        velocities.clear();
        lookAtTargets.clear();
        setVelocityCalls = 0;
        getVelocityCalls = 0;
        lookAtCalls = 0;
        returnNullVelocity = false;
    }
}
