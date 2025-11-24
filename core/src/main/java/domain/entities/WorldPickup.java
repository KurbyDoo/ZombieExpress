package domain.entities;

import com.badlogic.gdx.math.Vector3;
import domain.items.Item;

public class WorldPickup {

    private final Item item;
    private final Vector3 position;

    public WorldPickup(Item item, Vector3 position) {
        this.item = item;
        this.position = new Vector3(position);
    }

    public Item getItem() {
        return item;
    }

    public Vector3 getPosition() {
        return new Vector3(position);
    }
}
