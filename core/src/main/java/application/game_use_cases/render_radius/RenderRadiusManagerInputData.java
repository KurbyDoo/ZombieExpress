package application.game_use_cases.render_radius;

import domain.GamePosition;
import domain.World;

public class RenderRadiusManagerInputData {
    private final GamePosition playerPosition;
    private final int renderRadius;

    public RenderRadiusManagerInputData(
        GamePosition playerPosition,
        int radius
    ) {

        this.playerPosition = playerPosition;
        this.renderRadius = radius;
    }

    public GamePosition getPlayerPosition() {
        return playerPosition;
    }

    public int getRenderRadius() {
        return renderRadius;
    }
}
