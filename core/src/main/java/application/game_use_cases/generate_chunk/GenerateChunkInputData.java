package application.game_use_cases.generate_chunk;

import domain.GamePosition;
import domain.World;

public class GenerateChunkInputData {
    private final GamePosition position;
    private final int worldEndX;
    public GenerateChunkInputData(GamePosition position, int worldEndX) {
        this.position = position;
        this.worldEndX = worldEndX;
    }

    public GamePosition getPosition() { return position; }
    public int getWorldEndX() { return worldEndX; }
}
