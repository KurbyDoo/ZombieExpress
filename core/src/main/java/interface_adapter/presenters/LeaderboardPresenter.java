package interface_adapter.presenters;

import application.account_features.leaderboard.LeaderboardOutputBoundary;
import application.account_features.leaderboard.LeaderboardOutputData;
import interface_adapter.view_models.LeaderboardViewModel;

public class LeaderboardPresenter implements LeaderboardOutputBoundary {

    private final LeaderboardViewModel viewModel;

    public LeaderboardPresenter(LeaderboardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(LeaderboardOutputData data) {
        viewModel.setLeaderboard(data.getPlayers());
    }
}
