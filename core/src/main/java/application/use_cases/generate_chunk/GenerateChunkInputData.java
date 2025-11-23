package application.use_cases.generate_chunk;

import com.badlogic.gdx.math.Vector3;
import domain.World;

public class GenerateChunkInputData {
    private final Vector3 position;
    private final World world;
    public GenerateChunkInputData(Vector3 position, World world) {
        this.position = position;
        this.world = world;
    }

    public Vector3 getPosition() { return position; }
    public World getWorld() { return world; }
}
