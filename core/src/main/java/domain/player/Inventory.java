/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Aggregate Pattern: Inventory aggregates InventorySlots.
 * - Collection Pattern: Manages a fixed-size array of slots.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages inventory slot collection.
 * - [PASS] OCP: Slot behavior can be extended via InventorySlot.
 * - [N/A] LSP: No inheritance.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: No high-level dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Constants follow UPPER_SNAKE_CASE.
 * - [PASS] Javadoc present for key methods.
 * - [PASS] Proper exception handling for invalid indices.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 * - [PASS] Good documentation for addItem and removeItem methods.
 * - [WARN] Extra blank lines at end of file (lines 63-65).
 */
package domain.player;

import domain.items.Item;

public class Inventory {

    private static final int MAX_SLOTS = 10;
    private final InventorySlot[] slots = new InventorySlot[MAX_SLOTS];

    public Inventory() {
        for (int i = 0; i < MAX_SLOTS; i++) {
            slots[i] = new InventorySlot();
        }
    }

    public int getSize() {
        return MAX_SLOTS;
    }

    public InventorySlot getSlot(int index) {
        if (index < 0 || index >= MAX_SLOTS) {
            throw new IndexOutOfBoundsException("Invalid slot index: " + index);
        }
        return slots[index];
    }

    /**
     * Adds the specified item to the inventory.
     * Do nothing if inventory is full.
     * @param item The item to add to the inventory.
     */
    public boolean addItem(Item item) {
        if (item.isStackable()) {
            for (InventorySlot slot : slots) {
                if (!slot.isEmpty() && slot.getItem().equals(item)) {
                    slot.addOne(item);
                    return true;
                }
            }
        }

        for (InventorySlot slot : slots) {
            if (slot.isEmpty()) {
                slot.addOne(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes one instance of the item from the specified inventory slot.
     * Do nothing if the slot is empty.
     * @param slotIndex The index of the inventory slot.
     */
    public void removeItem(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= MAX_SLOTS) return;

        InventorySlot slot = slots[slotIndex];
        slot.removeOne();
    }
}


