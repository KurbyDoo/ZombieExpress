package application.use_cases.dismount_entity;

import domain.entities.Rideable;

public class DismountEntityInputData {
    private final Rideable ride;

    public DismountEntityInputData(Rideable ride) {
        this.ride = ride;
    }

    public Rideable getRide() {
        return ride;
    }
}
