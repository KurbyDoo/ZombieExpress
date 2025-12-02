package application.interface_use_cases.leaderboard;

import domain.player.PlayerData;

import java.util.List;

public class LeaderboardOutputData {
    private final List<PlayerData> players;
    public LeaderboardOutputData(List<PlayerData> players) {
        this.players = players;
    }
    public List<PlayerData> getPlayers() {
        return players;
    }

}
