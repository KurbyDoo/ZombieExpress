package application.game_features.update_world;

import domain.world.Chunk;
import domain.world.GamePosition;
import java.util.List;
import java.util.Map;

public class UpdateWorldOutputData {
    private final Map<GamePosition, Chunk> chunksToLoad;
    private final Map<GamePosition, Chunk> chunksToUnload;
    private final List<Integer> activeEntities;

    public UpdateWorldOutputData(
        Map<GamePosition, Chunk> toLoad,
        Map<GamePosition, Chunk> toUnload,
        List<Integer> activeEntities
    ) {
        chunksToLoad = toLoad;
        chunksToUnload = toUnload;
        this.activeEntities = activeEntities;
    }

    public Map<GamePosition, Chunk> getChunksToLoad() {
        return chunksToLoad;
    }

    public Map<GamePosition, Chunk> getChunksToUnload() {
        return chunksToUnload;
    }

    public List<Integer> getActiveEntities() {
        return activeEntities;
    }
}
