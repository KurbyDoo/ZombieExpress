package application.game_features.update_world;

public class UpdateWorldInputData {
    private final int renderRadius;

    public UpdateWorldInputData(int renderRadius) {
        this.renderRadius = renderRadius;
    }

    public int getRenderRadius() {
        return renderRadius;
    }
}
