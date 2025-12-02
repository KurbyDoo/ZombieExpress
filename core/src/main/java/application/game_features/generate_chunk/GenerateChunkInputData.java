package application.game_features.generate_chunk;

import domain.world.GamePosition;

public class GenerateChunkInputData {
    private final GamePosition position;
    private final int worldEndX;

    public GenerateChunkInputData(GamePosition position, int worldEndX) {
        this.position = position;
        this.worldEndX = worldEndX;
    }

    public GamePosition getPosition() {
        return position;
    }

    public int getWorldEndX() {
        return worldEndX;
    }
}
