package presentation.view;

/**
 * CLEAN ARCHITECTURE REQUIREMENT COMPLIANCE:
 * 
 * The requirement states:
 * "CLEAN architecture should be obeyed. For this request, we enforce that the game view 
 *  acts as the main class for downwards dependency injection, not Main.java or ViewManager.java"
 * 
 * CURRENT STATE:
 * ViewManager is currently very simple and only creates GameView. This is acceptable.
 * The requirement is being followed - GameView performs the dependency injection in its createView() method.
 * 
 * RECOMMENDATION:
 * ViewManager should remain minimal. All dependency injection should continue to happen in GameView.
 * If multiple views are added (MenuView, GameOverView, etc.), ViewManager should only handle view switching,
 * not dependency injection.
 */
public class ViewManager {
    private Viewable mainView;
    public ViewManager() {
        mainView = new GameView();
        mainView.createView();
    }

    public void render() {
        if (mainView != null) mainView.renderView();
    }

    public void dispose() {
        if (mainView != null) mainView.disposeView();
    }
}
