package application.use_cases.update_entity;

import application.use_cases.ports.PhysicsControlPort;
import domain.player.Player;

/**
 * Holds all external dependencies and data an entity might need
 * to decide how to act (Time, Player position, Physics access).
 */
public class BehaviourContext {
    private final PhysicsControlPort physics;
    private final Player player;
    private final float deltaTime;

    public BehaviourContext(PhysicsControlPort physics, Player player, float deltaTime) {
        this.physics = physics;
        this.player = player;
        this.deltaTime = deltaTime;
    }

    public PhysicsControlPort getPhysics() {
        return physics;
    }

    public Player getPlayer() {
        return player;
    }

    public float getDeltaTime() {
        return deltaTime;
    }
}
