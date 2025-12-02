package application.game_features.mount_entity;

public class MountEntityOutputData {
    private final boolean mountSuccess;

    public MountEntityOutputData(boolean mountSuccess) {
        this.mountSuccess = mountSuccess;
    }

    public boolean isMountSuccess() {
        return mountSuccess;
    }
}
