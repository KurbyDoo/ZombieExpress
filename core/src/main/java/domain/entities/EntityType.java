/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Enumeration Pattern: Type-safe enumeration for entity types.
 * - Part of Strategy Pattern: Used as discriminator for entity behavior selection.
 * - Part of Factory Pattern: Used by EntityFactory to select creation strategy.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain enumeration.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines entity type constants.
 * - [PASS] OCP: Can add new entity types without modifying existing code.
 * - [N/A] LSP: N/A for enums.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: No dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Enum name follows PascalCase.
 * - [PASS] Enum constants follow UPPER_SNAKE_CASE.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc describing what entity types are available.
 * - [PASS] Proper enum structure.
 */
package domain.entities;

public enum EntityType {
    CHUNK,
    ZOMBIE,
    BULLET,
    PICKUP,
    TRAIN
}
