package application.game_features.generate_entity.player_entity;

import application.game_features.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;
import domain.world.GamePosition;

public class GeneratePlayerEntityInputData extends GenerateEntityInputData {

    public GeneratePlayerEntityInputData(GamePosition position) {
        super(EntityType.PLAYER, position);
    }

}
