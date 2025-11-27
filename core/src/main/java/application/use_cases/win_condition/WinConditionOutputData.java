package application.use_cases.win_condition;

/**
 * Data structure holding the result of the win condition check.
 */
public class WinConditionOutputData {
    private final boolean gameWon;
    private final String message;

    public WinConditionOutputData(boolean gameWon, String message) {
        this.gameWon = gameWon;
        this.message = message;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public String getMessage() {
        return message;
    }
}
