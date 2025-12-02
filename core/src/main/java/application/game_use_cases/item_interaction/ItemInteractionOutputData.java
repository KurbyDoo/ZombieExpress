package application.game_use_cases.item_interaction;

import domain.entities.Train;
import domain.player.Player;

public class ItemInteractionOutputData {

    private final Player player;
    private final Train train;

    ItemInteractionOutputData(Player player, Train train) {
        this.player = player;
        this.train = train;
    }
    public Train getTrain() {
        return train;
    }
}
