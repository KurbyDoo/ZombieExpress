package framework.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ViewManager {

    private Game game;
    private Screen currentScreen;

    public void setGame(Game game) {
        this.game = game;
    }

    public void switchTo(ViewType type) {

        if (currentScreen != null) {
            currentScreen.dispose();
        }

        switch (type) {
            case LOGIN:
                currentScreen = ViewFactory.createLoginView(this);
                break;

            case REGISTER:
                currentScreen = ViewFactory.createRegisterView(this);
                break;

            case LEADERBOARD:
                currentScreen = ViewFactory.createLeaderboardView(this);
                break;

            case GAME:
                currentScreen = ViewFactory.createGameScreen();
                break;
        }

        game.setScreen(currentScreen);
    }

    public void handleManualExit() {
        if (currentScreen != null && currentScreen instanceof GameScreen) {
            GameScreen gs = (GameScreen) currentScreen;
            gs.saveOnExit();

        }
    }
}
