/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Strategy Pattern: Concrete strategy for bullet behavior.
 * - Implements EntityBehaviour interface.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - handles bullet movement behavior.
 * - [PASS] OCP: Can add new behaviors without modifying existing code.
 * - [PASS] LSP: Correctly implements EntityBehaviour.
 * - [PASS] ISP: Implements focused EntityBehaviour interface.
 * - [N/A] DIP: No high-level dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] BULLET_SPEED should be 'static final' not just 'final'.
 * - [WARN] Direct cast to Bullet (line 23) could throw ClassCastException.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Instance constants should be static: 'private static final float BULLET_SPEED'.
 * - [WARN] Commented-out code (lines 15-20) should be removed.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.update_entity;

import domain.GamePosition;
import domain.entities.Bullet;
import domain.entities.Entity;

public class BulletBehaviour implements EntityBehaviour {
    private final float BULLET_SPEED = 30.0f;
    private final GamePosition tempDir = new GamePosition();

    @Override
    public void execute(EntityBehaviourInputData inputData) {
        // Bullets usually have their velocity set once at creation.
        // But if we need constant propulsion (like a rocket):

        // Assuming the physics body is already rotated correctly:
        // context.physics.applyCentralForce(entity.getId(), ...);

        // Or simple timeout logic:
        // if (entity.getAge() > 5.0f) destroy(entity);
        // Find player direction

        tempDir.set(((Bullet)inputData.getEntity()).getDirection());

        tempDir.y = 0;
        tempDir.nor();

        // Get physics state
        inputData.getEntity().setVelocity(
            tempDir.x * BULLET_SPEED,
            0,
            tempDir.z * BULLET_SPEED
        );

        // Apply movement
        float yaw = (float) Math.toDegrees(Math.atan2(-tempDir.x, -tempDir.z));
        inputData.getEntity().setYaw(yaw);
    }
}
