package application.account_features.player_data;

public class SavePlayerDataInputData {
    private final int lastScore;

    public SavePlayerDataInputData(int lastScore) {
        this.lastScore = lastScore;
    }

    public int getLastScore() {
        return lastScore;
    }
}
