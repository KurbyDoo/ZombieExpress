/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Enumeration Pattern: Type-safe enumeration for ammo types.
 * - This enum is part of the Strategy pattern used in weapon handling.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain enumeration.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines ammo type constants.
 * - [PASS] OCP: Can be extended by adding new enum values.
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
 * - [MINOR] Missing class-level and field-level Javadoc.
 * - [PASS] Proper enum structure with private constructor.
 */
package domain;

public enum AmmoType {
    PISTOL("Pistol Ammo"),
    RIFLE("Rifle Ammo");

    private final String name;

    AmmoType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
