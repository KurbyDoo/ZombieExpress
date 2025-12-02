package domain.player;

public class PlayerData {
    private final String uid;
    private final String email;
    private final int lastScore;
    private final int highScore;

    public PlayerData(String uid, String email, int lastScore, int highScore) {
        this.uid = uid;
        this.email = email;
        this.lastScore = lastScore;
        this.highScore = highScore;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public int getLastScore() {
        return lastScore;
    }

    public int getHighScore() {
        return highScore;
    }
}
