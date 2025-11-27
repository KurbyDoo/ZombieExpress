package application.use_cases.generate_entity.pickup;

import application.use_cases.generate_entity.GenerateEntityInputData;
import com.badlogic.gdx.math.Vector3;
import domain.Chunk;
import domain.GamePosition;
import domain.entities.EntityType;
import domain.items.Item;

public class GeneratePickupInputData extends GenerateEntityInputData {

    private final Item item;

    public GeneratePickupInputData(Item item, GamePosition position) {
        super(EntityType.PICKUP, position);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
