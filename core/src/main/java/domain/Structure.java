package domain;

import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class Structure {
    // TODO: What does the structure object hold?
    private Vector3 position;
    private List<Vector3> entitySpawnPositions;
    private List<Vector3> spawnPositions;

    public Structure(Vector3 position, List<Vector3> entitySpawnPositions, List<Vector3> spawnPositions) {
        this.position = position;
        entitySpawnPositions = entitySpawnPositions;
        spawnPositions = spawnPositions;
    }
}
