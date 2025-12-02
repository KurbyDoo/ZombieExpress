package interface_adapter.Leaderboard;

import application.interface_use_cases.leaderboard.LeaderboardInputBoundary;
import presentation.view.ViewManager;
import presentation.view.ViewType;

public class LeaderboardController {

    private final LeaderboardInputBoundary interactor;
    private final ViewManager viewManager;

    public LeaderboardController(LeaderboardInputBoundary interactor,
                                 ViewManager viewManager) {
        this.interactor = interactor;
        this.viewManager = viewManager;
    }

    public void load() {
        interactor.loadLeaderboard();
    }

    public void startGame() {
        viewManager.switchTo(ViewType.GAME);
    }
}
