package presentation.view;

import com.badlogic.gdx.ScreenAdapter;

public class GameScreen extends ScreenAdapter {

    private final GameView gameView;

    public GameScreen() {
        this.gameView = new GameView();
    }

    @Override
    public void show() {
        gameView.createView();
    }

    @Override
    public void render(float delta) {
        gameView.renderView();
    }

    @Override
    public void dispose() {
        gameView.disposeView();
    }
}
