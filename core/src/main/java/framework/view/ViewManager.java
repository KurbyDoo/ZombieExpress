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

            case GAME:
                currentScreen = new GameScreen();
                break;
        }

        game.setScreen(currentScreen);
    }
}
