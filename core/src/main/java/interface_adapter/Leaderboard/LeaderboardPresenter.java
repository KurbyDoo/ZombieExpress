package interface_adapter.Leaderboard;

import application.interface_use_cases.Leaderboard.LeaderboardOutputBoundary;
import application.interface_use_cases.Leaderboard.LeaderboardOutputData;

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
