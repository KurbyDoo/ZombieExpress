package application.game_use_cases.render_radius;

import domain.GamePosition;
import domain.World;

public class RenderRadiusManagerInputData {
    private final GamePosition playerPosition;
    private final World world;
    private final int renderRadius;

    public RenderRadiusManagerInputData(
        GamePosition playerPosition,
        World world,
        int radius
    ) {

        this.playerPosition = playerPosition;
        this.world = world;
        this.renderRadius = radius;
    }

    public GamePosition getPlayerPosition() {
        return playerPosition;
    }

    public World getWorld() {
        return world;
    }

    public int getRenderRadius() {
        return renderRadius;
    }
}
