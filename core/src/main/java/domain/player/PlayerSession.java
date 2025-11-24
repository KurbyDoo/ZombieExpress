package domain.player;

public class PlayerSession {
    private String uid;
    private String email;
    private int lastScore;
    private int heightScore;

    public PlayerSession() {
        this.uid = null;
        this.email = null;
        this.lastScore = 0;
        this.heightScore = 0;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLastScore() {
        return lastScore;
    }

    public void setLastScore(int lastScore) {
        this.lastScore = lastScore;
    }

    public int getHeightScore() {
        return heightScore;
    }

    public void setHeightScore(int heightScore) {
        this.heightScore = heightScore;
    }

    public void clear() {
        this.uid = null;
        this.email = null;
        this.lastScore = 0;
        this.heightScore = 0;
    }
}
