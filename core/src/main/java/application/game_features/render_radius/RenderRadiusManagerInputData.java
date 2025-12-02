package application.game_features.render_radius;

import domain.world.GamePosition;

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
