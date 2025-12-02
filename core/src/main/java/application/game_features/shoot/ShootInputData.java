package application.game_features.shoot;

import domain.world.GamePosition;

public class ShootInputData {
    private GamePosition playerPos;
    private GamePosition playerDir;

    public ShootInputData(GamePosition playerPos, GamePosition playerDir) {
        this.playerPos = playerPos;
        this.playerDir = playerDir;
    }

    public GamePosition getPlayerPos() {
        return playerPos;
    }

    public GamePosition getPlayerDir() {
        return playerDir;
    }
}
