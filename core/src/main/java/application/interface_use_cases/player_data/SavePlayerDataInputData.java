package application.interface_use_cases.player_data;

public class SavePlayerDataInputData {
    private final int lastScore;

    public SavePlayerDataInputData(int lastScore) {
        this.lastScore = lastScore;
    }

    public int getLastScore() {
        return lastScore;
    }
}
