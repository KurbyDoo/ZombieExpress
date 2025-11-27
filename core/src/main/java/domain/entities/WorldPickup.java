package domain.entities;

import domain.GamePosition;
import domain.items.Item;

public class WorldPickup {

    private final Item item;
    private final GamePosition position;

    public WorldPickup(Item item, GamePosition position) {
        this.item = item;
        this.position = new GamePosition(position);
    }

    public Item getItem() {
        return item;
    }

    public GamePosition getPosition() {
        return new GamePosition(position);
    }
}
