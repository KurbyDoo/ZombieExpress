package interface_adapter.controllers;

import application.account_features.leaderboard.LeaderboardInputBoundary;
import framework.view.ViewManager;
import framework.view.ViewType;

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
