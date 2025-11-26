package application.use_cases.close_game;

/**
 * Defines the contract for initiating the game shutdown sequence.
 * This is used by the Presentation Layer (e.g., GameInputAdapter) to request an exit.
 */
public interface CloseGameInputBoundary {
    /**
     * Executes the use case to safely close the game application.
     */
    void execute();
}

