package application.game_use_cases.generate_entity.player_entity;

import application.game_use_cases.generate_entity.GenerateEntityInputData;
import domain.GamePosition;
import domain.entities.EntityType;
import infrastructure.rendering.strategies.GeneratePlayerEntityMeshStrategy;

public class GeneratePlayerEntityInputData extends GenerateEntityInputData {

    public GeneratePlayerEntityInputData( GamePosition position){
        super(EntityType.PLAYER, position);
    }

}
