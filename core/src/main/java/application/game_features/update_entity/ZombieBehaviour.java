package application.game_features.update_entity;

import domain.entities.Entity;
import domain.player.Player;
import domain.world.GamePosition;

public class ZombieBehaviour implements EntityBehaviour {
    private final Player player;

    private final float MOVE_SPEED = 3.0f;
    private final GamePosition tempDir = new GamePosition();
    private final GamePosition tempVel = new GamePosition();

    public ZombieBehaviour(Player player) {
        this.player = player;
    }

    @Override
    public void execute(EntityBehaviourInputData inputData) {
        Entity entity = inputData.getEntity();

        // Find player direction
        tempDir.set(player.getPosition()).sub(entity.getPosition());
        tempDir.y = 0;
        tempDir.nor();

        // Get physics state
        entity.setVelocity(
            tempDir.x * MOVE_SPEED,
            0,
            tempDir.z * MOVE_SPEED
        );

        // Apply movement
        float yaw = (float) Math.toDegrees(Math.atan2(-tempDir.x, -tempDir.z));
        entity.setYaw(yaw);
    }
}
