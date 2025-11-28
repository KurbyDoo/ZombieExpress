package application.game_use_cases.generate_chunk;

import domain.GamePosition;
import domain.World;

public class GenerateChunkInputData {
    private final GamePosition position;
    private final World world;
    public GenerateChunkInputData(GamePosition position, World world) {
        this.position = position;
        this.world = world;
    }

    public GamePosition getPosition() { return position; }
    public World getWorld() { return world; }
}
