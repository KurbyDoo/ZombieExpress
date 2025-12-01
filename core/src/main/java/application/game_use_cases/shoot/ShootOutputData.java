package application.game_use_cases.shoot;

import domain.GamePosition;

public class ShootOutputData {
    private int entityId;
    private GamePosition bulletSpawnPos;
    private GamePosition bulletDir;

    public ShootOutputData(int entityId, GamePosition bulletSpawnPos, GamePosition bulletDir) {
        this.entityId = entityId;
        this.bulletSpawnPos = bulletSpawnPos;
        this.bulletDir = bulletDir;
    }

    public int getEntityId() {
        return entityId;
    }

    public GamePosition getBulletSpawnPos() {
        return bulletSpawnPos;
    }

    public GamePosition getBulletDir() {
        return bulletDir;
    }
}
