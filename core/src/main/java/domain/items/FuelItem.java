/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Inheritance: Extends Item base class.
 * - Template Method: Overrides toString() from Item.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages fuel-specific data (fuel value).
 * - [PASS] OCP: Extends Item without modifying it.
 * - [PASS] LSP: Can substitute for Item.
 * - [N/A] ISP: No interfaces implemented.
 * - [N/A] DIP: No dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Properly calls super constructor.
 * - [PASS] toString() provides meaningful output.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level and method-level Javadoc.
 * - [PASS] Proper use of final for immutable fields.
 */
package domain.items;

public class FuelItem extends Item {

    private final int fuelValue;

    public FuelItem(String name, boolean stackable, int fuelValue) {
        super(name, stackable);
        this.fuelValue = fuelValue;
    }

    public int getFuelValue() {
        return fuelValue;
    }

    @Override
    public String toString() {
        return getName() + " - Fuel: " + fuelValue;
    }
}
