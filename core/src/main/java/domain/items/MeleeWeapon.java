/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Inheritance: Extends Weapon abstract class.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Represents melee weapon type.
 * - [PASS] OCP: Extends Weapon without modifying it.
 * - [PASS] LSP: Can substitute for Weapon and Item.
 * - [N/A] ISP: No interfaces implemented.
 * - [N/A] DIP: No dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] toString() provides meaningful output.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 * - [PASS] Simple, focused class.
 */
package domain.items;

public class MeleeWeapon extends Weapon {

    public MeleeWeapon(String name, int damage) {
        super(name, damage);
    }

    @Override
    public String toString() {
        return getName() + " [Melee] (Damage: " + damage + ")";
    }
}
