package domain.entities;

import com.badlogic.gdx.math.Vector3;
import domain.GamePosition;
import domain.items.Item;

public class PickupEntity extends Entity {

    private final Item item;

    public PickupEntity(Integer id, Item item, GamePosition position, boolean visible) {
        super(id, EntityType.PICKUP, new GamePosition(position), visible);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
