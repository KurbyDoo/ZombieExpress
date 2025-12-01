/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Strategy Pattern Interface: Defines contract for entity behaviors.
 * - Part of Behavioral Pattern family.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case interface.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines behavior contract.
 * - [PASS] OCP: New behaviors implement this interface.
 * - [PASS] ISP: Minimal, focused interface (single method).
 * - [PASS] DIP: High-level modules depend on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing interface-level Javadoc.
 */
package application.game_use_cases.update_entity;

public interface EntityBehaviour {
    void execute(EntityBehaviourInputData inputData);
}
