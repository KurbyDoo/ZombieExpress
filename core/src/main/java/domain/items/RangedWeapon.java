/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Inheritance: Extends Weapon abstract class.
 * - Composition: Contains AmmoType reference.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Represents ranged weapon with ammo requirement.
 * - [PASS] OCP: Extends Weapon without modifying it.
 * - [PASS] LSP: Can substitute for Weapon and Item.
 * - [N/A] ISP: No interfaces implemented.
 * - [N/A] DIP: No dependencies to invert.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] toString() provides meaningful output with ammo type.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level and method-level Javadoc.
 * - [PASS] Proper use of final for immutable fields.
 */
package domain.items;

import domain.AmmoType;

public class RangedWeapon extends Weapon {

    private final AmmoType ammoType;

    public RangedWeapon(String name, int damage, AmmoType ammoType) {
        super(name, damage);
        this.ammoType = ammoType;
    }

    public AmmoType getAmmoType() {
        return ammoType;
    }

    @Override
    public String toString() {
        return getName() + " [Ranged] (Damage: " + damage + ", Ammo: " + ammoType.getName() + ")";
    }
}
