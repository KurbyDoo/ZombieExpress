package presentation.view;

import application.interface_use_cases.player_data.PlayerDataAccessInterface;
import application.interface_use_cases.player_data.SavePlayerDataInteractor;
import com.badlogic.gdx.ScreenAdapter;
import domain.player.PlayerSession;

public class GameScreen extends ScreenAdapter {

    private final GameView gameView;

    public GameScreen(PlayerSession session,SavePlayerDataInteractor saveScore) {
        this.gameView = new GameView(session,saveScore);
    }

    public void saveOnExit() {
        System.out.println("[GameScreen] Manual exit detected");
        gameView.saveOnExit();
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
