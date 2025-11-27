package application.use_cases.render_radius;

import domain.GamePosition;

import java.util.HashSet;
import java.util.Set;

public class RenderRadiusOutputData {
    private final Set<GamePosition> chunksToGenerate;
    private final Set<GamePosition> chunksToLoad;
    private final Set<GamePosition> chunksToUnload;
    private final Set<GamePosition> chunksToUpdate;
    public RenderRadiusOutputData() {
        chunksToGenerate = new HashSet<>();
        chunksToLoad = new HashSet<>();
        chunksToUnload = new HashSet<>();
        chunksToUpdate = new HashSet<>();
    }

    public Set<GamePosition> getChunksToGenerate() {
        return chunksToGenerate;
    }

    public Set<GamePosition> getChunksToLoad() {
        return chunksToLoad;
    }

    public Set<GamePosition> getChunksToUnload() {
        return chunksToUnload;
    }

    public Set<GamePosition> getChunksToUpdate() {
        return chunksToUpdate;
    }
}
