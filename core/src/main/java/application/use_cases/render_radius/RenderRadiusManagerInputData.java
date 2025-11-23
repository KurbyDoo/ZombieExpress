package application.use_cases.render_radius;

import com.badlogic.gdx.math.Vector3;
import domain.World;

public class RenderRadiusManagerInputData {
    private final Vector3 playerPosition;
    private final World world;
    private final int renderRadius;

    public RenderRadiusManagerInputData(
        Vector3 playerPosition,
        World world,
        int radius
    ) {

        this.playerPosition = playerPosition;
        this.world = world;
        this.renderRadius = radius;
    }

    public Vector3 getPlayerPosition() {
        return playerPosition;
    }

    public World getWorld() {
        return world;
    }

    public int getRenderRadius() {
        return renderRadius;
    }
}
