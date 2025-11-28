package application.use_cases.update_world;

import domain.GamePosition;

import java.util.Set;

public class UpdateWorldOutputData {
    private final Set<GamePosition> chunksToLoad;
    private final Set<GamePosition> chunksToUpdate;
    private final Set<GamePosition> chunksToUnload;

    public UpdateWorldOutputData(Set<GamePosition> toLoad, Set<GamePosition> toUpdate, Set<GamePosition> toUnload) {
        chunksToLoad = toLoad;
        chunksToUpdate = toUpdate;
        chunksToUnload = toUnload;
    }

    public Set<GamePosition> getChunksToLoad() {
        return chunksToLoad;
    }

    public Set<GamePosition> getChunksToUpdate() {
        return chunksToUpdate;
    }

    public Set<GamePosition> getChunksToUnload() {
        return chunksToUnload;
    }
}
