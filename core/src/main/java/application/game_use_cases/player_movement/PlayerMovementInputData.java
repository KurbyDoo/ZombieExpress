/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Data Pattern: Encapsulates all input for player movement use case.
 * - Immutable Data Transfer Object (DTO).
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure for use case input.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds movement input data.
 * - [PASS] OCP: Immutable, cannot be modified.
 * - [N/A] LSP: No inheritance.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: Pure data class.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Fields are private and final (immutable).
 * - [PASS] Boolean getters use 'is' prefix.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 * - [PASS] Single-line getters are acceptable for DTOs.
 */
package application.game_use_cases.player_movement;

public class PlayerMovementInputData {
    private final boolean forward;
    private final boolean backward;
    private final boolean left;
    private final boolean right;
    private final boolean sprinting;
    private final float deltaX;
    private final float deltaY;
    private final float deltaTime;

    public PlayerMovementInputData(boolean forward, boolean backward, boolean left, boolean right, boolean sprinting, float deltaX, float deltaY, float deltaTime) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.sprinting = sprinting;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaTime = deltaTime;
    }

    // Getters for all fields
    public boolean isForward() { return forward; }
    public boolean isBackward() { return backward; }
    public boolean isLeft() { return left; }
    public boolean isRight() { return right; }
    public boolean isSprinting() { return sprinting; }
    public float getDeltaX() { return deltaX; }
    public float getDeltaY() { return deltaY; }
    public float getDeltaTime() { return deltaTime; }
}
