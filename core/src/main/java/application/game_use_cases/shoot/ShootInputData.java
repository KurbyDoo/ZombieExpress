package application.game_use_cases.shoot;

import domain.GamePosition;

public class ShootInputData {
    private GamePosition playerPos;

    public ShootInputData(GamePosition playerPos) {
        this.playerPos = playerPos;
    }

    public GamePosition getPlayerPos() {
        return playerPos;
    }
}
