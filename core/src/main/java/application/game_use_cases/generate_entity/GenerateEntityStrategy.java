/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Strategy Pattern Interface: Defines contract for entity creation.
 * - Factory Method Pattern: Each strategy acts as a factory for its entity type.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case interface.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines entity creation contract.
 * - [PASS] OCP: New entity types implement this interface.
 * - [PASS] ISP: Minimal, focused interface (single method).
 * - [PASS] DIP: EntityFactory depends on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [WARN] TODO comment (line 6) indicates incomplete design.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] TODO/FIXME comments should be addressed.
 * - [MINOR] Missing interface-level Javadoc.
 */
package application.game_use_cases.generate_entity;

import domain.entities.Entity;

public interface GenerateEntityStrategy {
    // Used for Factory - can be deleted later
    // Need stronger typing
    Entity execute(GenerateEntityInputData inputData);
}
