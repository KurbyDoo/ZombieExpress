package application.use_cases.render_radius;

import com.badlogic.gdx.math.Vector3;

import java.util.HashSet;
import java.util.Set;

public class RenderRadiusOutputData {
    private final Set<Vector3> chunksToGenerate;
    private final Set<Vector3> chunksToLoad;
    private final Set<Vector3> chunksToUnload;
    private final Set<Vector3> chunksToUpdate;
    public RenderRadiusOutputData() {
        chunksToGenerate = new HashSet<>();
        chunksToLoad = new HashSet<>();
        chunksToUnload = new HashSet<>();
        chunksToUpdate = new HashSet<>();
    }

    public RenderRadiusOutputData(
        Set<Vector3> chunksToGenerate,
        Set<Vector3> chunksToLoad,
        Set<Vector3> chunksToUnload,
        Set<Vector3> chunksToUpdate
    ) {
        this.chunksToGenerate = chunksToGenerate;
        this.chunksToLoad = chunksToLoad;
        this.chunksToUnload = chunksToUnload;
        this.chunksToUpdate = chunksToUpdate;
    }

    public Set<Vector3> getChunksToGenerate() {
        return chunksToGenerate;
    }

    public Set<Vector3> getChunksToLoad() {
        return chunksToLoad;
    }

    public Set<Vector3> getChunksToUnload() {
        return chunksToUnload;
    }

    public Set<Vector3> getChunksToUpdate() {
        return chunksToUpdate;
    }
}
