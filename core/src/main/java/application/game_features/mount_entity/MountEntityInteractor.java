package application.game_features.mount_entity;

import domain.entities.Rideable;
import domain.player.Player;
import domain.world.GamePosition;

public class MountEntityInteractor implements MountEntityInputBoundary {
    private final Player player;

    public MountEntityInteractor(Player player) {
        this.player = player;
    }

    @Override
    public MountEntityOutputData execute(MountEntityInputData inputData) {
        Rideable ride = inputData.getRide();
        if (player.getCurrentRide() != null) {
            return new MountEntityOutputData(false);
        }

        if (ride == null) {
            return new MountEntityOutputData(false);
        }

        player.setCurrentRide(ride);
        GamePosition seatOffset = ride.getRideOffset();
        GamePosition newPlayerPos = ride.getPosition().cpy().add(seatOffset);
        player.setPosition(newPlayerPos);
        return new MountEntityOutputData(true);
    }
}
