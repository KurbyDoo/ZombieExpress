package application.game_features.generate_entity.pickup;

import application.game_features.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;
import domain.items.Item;
import domain.world.GamePosition;

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
