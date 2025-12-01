/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Output Data Pattern (DTO): Transfers camera data to controllers.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data transfer object.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds camera data.
 * - [PASS] Immutable data class (final fields).
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Good Javadoc documentation present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Well-documented class.
 */
package application.game_use_cases.query_camera_data;

import domain.GamePosition;

/**
 * Data Transfer Object (DTO) used to transfer minimal, necessary data
 * from the Application layer to the Interface Adapter (Controller) for camera positioning.
 */
public class CameraDataOutput {
    private final GamePosition position;
    private final GamePosition direction;
    private final GamePosition up;

    public CameraDataOutput(GamePosition position, GamePosition direction, GamePosition up) {
        this.position = position;
        this.direction = direction;
        this.up = up;
    }

    public GamePosition getPosition() {
        return position;
    }

    public GamePosition getDirection() {
        return direction;
    }

    public GamePosition getUp() {
        return up;
    }
}

