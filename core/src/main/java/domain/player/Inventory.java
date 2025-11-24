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
    public void addItem(Item item, int quantity) {
        if (item.isStackable()) {
            for (InventorySlot slot : slots) {
                if (!slot.isEmpty() && slot.getItem().equals(item)) {
                    for (int i = 0; i < quantity; i++) {
                        slot.addOne(item);
                    }
                    return;
                }
            }
        }

        for (InventorySlot slot : slots) {
            if (slot.isEmpty()) {
                for (int i = 0; i < quantity; i++) {
                    slot.addOne(item);
                }
                return;
            }
        }
    }

    public void addItem(Item item) {
        addItem(item, 1);
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


