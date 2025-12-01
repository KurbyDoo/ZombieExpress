/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Port Pattern (Clean Architecture): Defines abstraction for framework lifecycle.
 * - Dependency Inversion: High-level policy depends on this abstraction.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure port interface - EXCELLENT example of Clean Architecture.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - application lifecycle control.
 * - [PASS] ISP: Minimal, focused interface.
 * - [PASS] DIP: High-level modules depend on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [PASS] Good Javadoc documentation present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Well-documented interface.
 */
package application.game_use_cases.ports;

/**
 * Port interface defining system-level application operations required by Use Cases.
 * This decouples the Application Layer from the underlying framework (e.g., LibGDX).
 */
public interface ApplicationLifecyclePort {
    /**
     * Initiates the shutdown sequence for the application.
     */
    void closeApplication();
}
