/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Output Data Pattern: Encapsulates shooting use case output.
 * - Data Transfer Object (DTO).
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure for use case output.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds shooting output data.
 * - [N/A] LSP: No inheritance.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: Pure data class.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Fields should be final for immutability.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Fields should be declared final.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.shoot;

import domain.GamePosition;

public class ShootOutputData {
    private int entityId;
    private GamePosition bulletSpawnPos;
    private GamePosition bulletDir;

    public ShootOutputData(int entityId, GamePosition bulletSpawnPos, GamePosition bulletDir) {
        this.entityId = entityId;
        this.bulletSpawnPos = bulletSpawnPos;
        this.bulletDir = bulletDir;
    }

    public int getEntityId() {
        return entityId;
    }

    public GamePosition getBulletSpawnPos() {
        return bulletSpawnPos;
    }

    public GamePosition getBulletDir() {
        return bulletDir;
    }
}
