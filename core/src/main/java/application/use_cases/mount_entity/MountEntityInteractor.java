package application.use_cases.mount_entity;

import domain.entities.Rideable;
import domain.player.Player;

public class MountEntityInteractor implements MountEntityInputBoundary {
    private final Player player;

    public MountEntityInteractor(Player player) {
        this.player = player;
    }
    @Override
    public MountEntityOutputData execute(MountEntityInputData inputData) {
        Rideable ride = inputData.getRide();
        if (player.getCurrentRide() == null) {
            return new MountEntityOutputData(false);
        }

        player.setCurrentRide(ride);
        return new MountEntityOutputData(true);
    }
}
