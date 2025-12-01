/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Query Pattern: Retrieves camera data from player entity.
 * - Implements Input Boundary for Clean Architecture.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - queries player camera data.
 * - [PASS] LSP: Correctly implements QueryCameraDataInputBoundary.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Good Javadoc documentation present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Well-documented class.
 */
package application.game_use_cases.query_camera_data;

import domain.player.Player;

/**
 * Concrete implementation of the QueryCameraDataInputBoundary.
 * This class fetches the current position, direction, and up vector directly
 * from the Player entity and wraps it in the required CameraDataOutput DTO.
 */
public class PlayerCameraDataQuery implements QueryCameraDataInputBoundary {

    private final Player player;

    public PlayerCameraDataQuery(Player player) {
        this.player = player;
    }

    /**
     * Executes the query to get the current camera data.
     * @return A CameraDataOutput object containing the player's current GamePosition fields.
     */
    @Override
    public CameraDataOutput execute() {
        // The Player object implicitly holds the current camera state
        // (position, direction, and up vector).
        return new CameraDataOutput(
            player.getPosition(),
            player.getDirection(),
            player.getUp()
        );
    }
}
