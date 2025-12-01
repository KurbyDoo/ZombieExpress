package application.game_features.win_condition;

/**
 * Defines the contract for checking if the win condition has been met.
 * The use case should handle the logic for determining the win and initiating
 * any necessary game shutdown or state change.
 */
public interface WinConditionInputBoundary {
    /**
     * Executes the check for the win condition.
     * @return Output data indicating if the game was won and providing a message.
     */
    WinConditionOutputData execute();
}
