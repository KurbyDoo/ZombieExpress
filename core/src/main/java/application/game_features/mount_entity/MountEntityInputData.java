package application.game_features.mount_entity;

import domain.entities.Rideable;

public class MountEntityInputData {
    private final Rideable ride;

    public MountEntityInputData(Rideable ride) {
        this.ride = ride;
    }

    public Rideable getRide() {
        return ride;
    }
}
