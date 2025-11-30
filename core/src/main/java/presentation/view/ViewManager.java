package presentation.view;

import javax.swing.*;
import presentation.view.ViewFactory;

public class ViewManager {
    private ViewType currentView;
    private JFrame currentSwing;
    private Viewable gameView;
    public ViewManager() {
        switchTo(ViewType.LOGIN);
    }
    public void switchTo(ViewType viewType) {
        if (currentSwing != null) {
            currentSwing.dispose();
            currentSwing = null;
        }

        if (gameView != null) {
            gameView.disposeView();
            gameView = null;
        }
        currentView = viewType;
        switch (viewType) {
            case LOGIN:
                currentSwing = ViewFactory.createLoginView(this);
                break;

            case REGISTER:
                currentSwing = ViewFactory.createRegisterView(this);
                break;

            case GAME:
                gameView  = new GameView();
                gameView.createView();
                break;

        }
    }
    public void render() {
        if (gameView != null) gameView.renderView();
    }

    public void dispose() {
        if (currentView != null) currentSwing.dispose();
        if (gameView != null) gameView.disposeView();
    }
}
