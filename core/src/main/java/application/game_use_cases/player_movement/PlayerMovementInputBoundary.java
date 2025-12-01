/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Boundary Pattern: Defines interface for player movement use case.
 * - Part of Clean Architecture Use Case pattern.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case interface (Port).
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines movement input contract.
 * - [PASS] OCP: Can be implemented by multiple interactors.
 * - [PASS] LSP: Implementations must fulfill contract.
 * - [PASS] ISP: Focused interface with single method.
 * - [PASS] DIP: Controllers depend on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing interface-level Javadoc.
 */
package application.game_use_cases.player_movement;

public interface PlayerMovementInputBoundary {
    void execute(PlayerMovementInputData playerMovementInputData);
}
