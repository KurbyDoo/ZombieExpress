/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Presentation/Views)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
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
