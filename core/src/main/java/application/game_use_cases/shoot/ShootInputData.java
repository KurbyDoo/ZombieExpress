/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Data Pattern: Encapsulates shooting use case input.
 * - Data Transfer Object (DTO).
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure for use case input.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds shooting input data.
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
 * - [WARN] Fields should be declared final: 'private final GamePosition playerPos'.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.shoot;

import domain.GamePosition;

public class ShootInputData {
    private GamePosition playerPos;
    private GamePosition playerDir;

    public ShootInputData(GamePosition playerPos, GamePosition playerDir) {
        this.playerPos = playerPos;
        this.playerDir = playerDir;
    }

    public GamePosition getPlayerPos() {
        return playerPos;
    }

    public GamePosition getPlayerDir() {
        return playerDir;
    }
}
