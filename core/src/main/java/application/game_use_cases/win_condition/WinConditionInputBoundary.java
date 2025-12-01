/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Boundary Pattern: Defines contract for win condition use case.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case interface.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines win condition contract.
 * - [PASS] ISP: Focused interface with single method.
 * - [PASS] DIP: Higher-level modules depend on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [PASS] Good Javadoc documentation present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Well-documented interface.
 */
package application.game_use_cases.win_condition;

/**
 * Defines the contract for checking if the win condition has been met.
 * The use case should handle the logic for determining the win and initiating
 * any necessary game shutdown or state change.
 */
public interface WinConditionInputBoundary {
    /**
     * Executes the check for the win condition.
     * @return Output data indicating if the game was won and providing a message.
     */
    WinConditionOutputData execute();
}
