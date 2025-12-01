/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Value Object Pattern: Represents a single inventory slot.
 * - Container Pattern: Holds item and quantity.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages single slot data (item, quantity).
 * - [PASS] OCP: Behavior is self-contained.
 * - [N/A] LSP: No inheritance.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: No dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Methods follow camelCase.
 * - [PASS] Javadoc present for key methods.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 * - [PASS] Good method documentation.
 */
package domain.player;

import domain.items.Item;

public class InventorySlot {

    private Item item;
    private int quantity;

    public boolean isEmpty() {
        return item == null || quantity <= 0;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Adds one instance of the specified item to this inventory slot.
     * Do nothing if unable to add the item to this slot.
     * @param newItem The item to be added to this slot.
     */
    public void addOne(Item newItem) {
        if (isEmpty()) {
            item = newItem;
            quantity = 1;
            return;
        }
        if (item.equals(newItem) && item.isStackable()) {
            quantity++;
        }
    }

    /**
     * Removes one instance of the specified item from this inventory slot.
     * Do nothing if this slot is empty.
     */
    public void removeOne() {
        if (isEmpty()) return;

        quantity--;
        if (quantity <= 0) {
            item = null;
            quantity = 0;
        }
    }
}
