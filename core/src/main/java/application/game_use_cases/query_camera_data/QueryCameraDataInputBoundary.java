/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Boundary Pattern: Defines contract for camera data query.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case interface.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines camera query contract.
 * - [PASS] ISP: Focused interface with single method.
 * - [PASS] DIP: Controllers depend on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [PASS] Good Javadoc documentation present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Well-documented interface.
 */
package application.game_use_cases.query_camera_data;

/**
 * Defines the contract for querying the camera's required data (position/direction).
 */
public interface QueryCameraDataInputBoundary {
    /**
     * Executes the query and returns the necessary data for the camera controller.
     * @return CameraDataOutput DTO.
     */
    CameraDataOutput execute();
}
