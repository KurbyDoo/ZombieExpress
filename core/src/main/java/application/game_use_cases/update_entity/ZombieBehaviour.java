/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Strategy Pattern: Concrete strategy for zombie AI behavior.
 * - Implements EntityBehaviour interface.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - handles zombie movement toward player.
 * - [PASS] OCP: Can add new behaviors without modifying existing code.
 * - [PASS] LSP: Correctly implements EntityBehaviour.
 * - [PASS] ISP: Implements focused EntityBehaviour interface.
 * - [N/A] DIP: No high-level dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] MOVE_SPEED should be 'static final' not just 'final'.
 * - [WARN] tempDir and tempVel are mutable shared state (not thread-safe).
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Instance constants should be static: 'private static final float MOVE_SPEED'.
 * - [WARN] Unused field 'tempVel' (line 12).
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.update_entity;

import domain.GamePosition;
import domain.entities.Entity;
import domain.player.Player;

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
