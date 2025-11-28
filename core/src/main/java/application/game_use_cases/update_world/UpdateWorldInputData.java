package application.game_use_cases.update_world;

public class UpdateWorldInputData {
    private final int renderRadius;

    public UpdateWorldInputData(int renderRadius) {
        this.renderRadius = renderRadius;
    }

    public int getRenderRadius() {
        return renderRadius;
    }
}
