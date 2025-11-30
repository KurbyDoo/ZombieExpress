package application.game_use_cases.shoot;

public class ShootOutputData {
    private int entityId;

    public ShootOutputData(int entityId) {
        this.entityId = entityId;
    }

    public int getEntityId() {
        return entityId;
    }
}
