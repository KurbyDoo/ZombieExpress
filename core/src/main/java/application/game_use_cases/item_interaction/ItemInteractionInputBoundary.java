package application.game_use_cases.item_interaction;

import domain.entities.Train;

public interface ItemInteractionInputBoundary {
    ItemInteractionOutputData execute(ItemInteractionInputData inputData);
}

//package application.game_use_cases.dismount_entity;
//
//public interface DismountEntityInputBoundary {
//    DismountEntityOutputData execute(DismountEntityInputData inputData);
//}
