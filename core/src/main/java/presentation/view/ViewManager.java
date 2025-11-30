package presentation.view;

public class ViewManager {

    private static ViewManager instance;

    public static ViewManager getInstance() {
        return instance;
    }

    private ViewType currentViewType;
    private Viewable currentView;

    public ViewManager() {
        instance = this;
        switchTo(ViewType.LOGIN);
    }

    public void switchTo(ViewType viewType) {
        this.currentViewType = viewType;

        // dispose old view
        if (currentView != null) {
            currentView.disposeView();
        }

        switch (viewType) {
            case GAME:
                currentView = new GameView();
                currentView.createView();
                break;

            case LOGIN:
                currentView = null;
                break;
        }
    }

    public void render() {
        if (currentView != null) {
            currentView.renderView();
        }
    }

    public void dispose() {
        if (currentView != null) {
            currentView.disposeView();
        }
    }
}
