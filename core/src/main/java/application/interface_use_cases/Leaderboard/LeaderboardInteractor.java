package application.interface_use_cases.Leaderboard;

import application.interface_use_cases.player_data.PlayerDataAccessInterface;
import domain.player.PlayerData;

import java.util.List;

public class LeaderboardInteractor implements LeaderboardInputBoundary {

    private final PlayerDataAccessInterface dataAccess;
    private final LeaderboardOutputBoundary presenter;

    public LeaderboardInteractor(PlayerDataAccessInterface dataAccess,
                                 LeaderboardOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void loadLeaderboard() {
        List<PlayerData> players = dataAccess.getAllPlayers();
        players.sort((a, b) -> b.getHighScore() - a.getHighScore());
        List<PlayerData> top10 = players.subList(0, Math.min(10, players.size()));

        presenter.present(new LeaderboardOutputData(top10));
    }
}
