package application.use_cases.generate_entity.pickup;

import application.use_cases.generate_entity.GenerateEntityInputData;
import com.badlogic.gdx.math.Vector3;
import domain.Chunk;
import domain.entities.EntityType;
import domain.items.Item;

public class GeneratePickupInputData extends GenerateEntityInputData {

    private final Item item;

    public GeneratePickupInputData(Item item, Vector3 position, Chunk chunk) {
        super(EntityType.PICKUP, position, chunk);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
