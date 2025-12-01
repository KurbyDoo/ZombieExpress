/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Abstract Template: Defines common weapon behavior.
 * - Inheritance: Extends Item, subclassed by MeleeWeapon and RangedWeapon.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages weapon-specific data (damage).
 * - [PASS] OCP: Open for extension via MeleeWeapon, RangedWeapon.
 * - [PASS] LSP: Subclasses can substitute for Weapon and Item.
 * - [N/A] ISP: No interfaces implemented.
 * - [N/A] DIP: No dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Abstract class properly defined.
 * - [WARN] Protected field 'damage' - could use getter only for better encapsulation.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level and method-level Javadoc.
 * - [PASS] Properly marks weapons as non-stackable.
 */
package domain.items;

public abstract class Weapon extends Item {

    protected final int damage;

    public Weapon(String name, int damage) {
        super(name, false); // weapons aren't stackable
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public String toString() {
        return getName() + " (Damage: " + damage + ")";
    }
}
