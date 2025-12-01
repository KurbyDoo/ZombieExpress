/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Output Data Pattern: Encapsulates win condition result.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds win condition result.
 * - [PASS] Immutable data class (final fields).
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Good Javadoc documentation present.
 * - [PASS] Boolean getter uses 'is' prefix.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Well-documented class.
 */
package application.game_use_cases.win_condition;

/**
 * Data structure holding the result of the win condition check.
 */
public class WinConditionOutputData {
    private final boolean gameIsOver;
    private final String message;

    public WinConditionOutputData(boolean gameIsOver, String message) {
        this.gameIsOver = gameIsOver;
        this.message = message;
    }

    public boolean isGameOver() {
        return gameIsOver;
    }

    public String getMessage() {
        return message;
    }
}
