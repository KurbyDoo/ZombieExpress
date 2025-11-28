package application.game_use_cases.dismount_entity;

public class DismountEntityOutputData {
    private final boolean dismountSuccess;

    public DismountEntityOutputData(boolean dismountSuccess) {
        this.dismountSuccess = dismountSuccess;
    }

    public boolean isDismountSuccess() {
        return dismountSuccess;
    }
}
