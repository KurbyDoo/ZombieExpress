package interface_adapter.Leaderboard;

import domain.player.PlayerData;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class LeaderboardViewModel {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private List<PlayerData> leaderboard;

    public void setLeaderboard(List<PlayerData> leaderboard) {
        List<PlayerData> oldLeaderboard = this.leaderboard;
        this.leaderboard = leaderboard;
        propertyChangeSupport.firePropertyChange("leaderboard", oldLeaderboard, leaderboard);
    }

    public List<PlayerData> getLeaderboard() {
        return leaderboard;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
