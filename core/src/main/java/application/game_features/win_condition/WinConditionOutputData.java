package application.game_features.win_condition;

/**
 * Data structure holding the result of the win condition check.
 */
public class WinConditionOutputData {
    private final boolean gameIsOver;
    private final String message;
    private final int score;

    public WinConditionOutputData(boolean gameIsOver, String message, int score) {
        this.gameIsOver = gameIsOver;
        this.message = message;
        this.score = score;
    }

    public boolean isGameOver() {
        return gameIsOver;
    }

    public String getMessage() {
        return message;
    }

    public int getScore() {
        return score;
    }
}
