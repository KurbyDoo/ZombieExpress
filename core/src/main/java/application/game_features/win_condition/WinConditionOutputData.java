package application.game_features.win_condition;

/**
 * Data structure holding the result of the win condition check.
 */
public class WinConditionOutputData {
    private final boolean gameIsOver;
    private final String message;

    public WinConditionOutputData(boolean gameIsOver, String message) {
        this.gameIsOver = gameIsOver;
        this.message = message;
    }

    public boolean isGameOver() {
        return gameIsOver;
    }

    public String getMessage() {
        return message;
    }
}
