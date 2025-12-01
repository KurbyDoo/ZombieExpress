package application.game_features.dismount_entity;

import domain.GamePosition;
import domain.entities.Rideable;
import domain.player.Player;

public class DismountEntityInteractor implements DismountEntityInputBoundary {
    private final Player player;

    public DismountEntityInteractor(Player player) {
        this.player = player;
    }
    @Override
    public DismountEntityOutputData execute(DismountEntityInputData inputData) {
        if (player.getCurrentRide() == null) {
            return new DismountEntityOutputData(false);
        }

        Rideable ride = player.getCurrentRide();
        player.setCurrentRide(null);
        GamePosition seatOffset = ride.getRideOffset().cpy().add(0, -2f, 4f);
        player.setPosition(player.getPosition().sub(seatOffset));
        return new DismountEntityOutputData(true);
    }
}
