package domain.entities;

import com.badlogic.gdx.math.Vector3;
import domain.items.Item;

public class PickupEntity extends Entity {

    private final Item item;

    public PickupEntity(Integer id, Item item, Vector3 position, boolean visible) {
        super(id, EntityType.PICKUP, new Vector3(position), visible);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
