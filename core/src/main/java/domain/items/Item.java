/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Value Object Pattern: Represents an immutable item identity.
 * - Template Method potential: Base class for item hierarchy.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages basic item data (name, stackability).
 * - [PASS] OCP: Open for extension via subclasses (Weapon, FuelItem).
 * - [PASS] LSP: Subclasses can substitute for Item.
 * - [N/A] ISP: No interfaces implemented.
 * - [N/A] DIP: No dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Fields are private and final.
 * - [PASS] equals() and hashCode() properly implemented.
 * - [PASS] toString() provides meaningful output.
 * - [MINOR] Javadoc present but could be more detailed.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Well-structured class with proper encapsulation.
 * - [PASS] Javadoc present for equals() method.
 */
package domain.items;

import java.util.Objects;

public class Item {

    private final String name;
    private final boolean stackable;

    public Item(String name, boolean stackable) {
        this.name = name;
        this.stackable = stackable;
    }

    public String getName() {
        return name;
    }

    public boolean isStackable() {
        return stackable;
    }

    /**
     * Determines whether this item is equal to another object.
     * @param o The object to compare with this item.
     * @return True if the specified object is an Item with the same ID; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
