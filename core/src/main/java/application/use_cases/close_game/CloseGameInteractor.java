package application.use_cases.close_game;

import com.badlogic.gdx.Gdx;

/**
 * Handles the application logic for closing the game.
 * This keeps the low-level Gdx.app.exit() call out of the input adapter.
 */
public class CloseGameInteractor implements CloseGameInputBoundary {

    public CloseGameInteractor() {
        // No dependencies needed for a simple exit
    }

    @Override
    public void execute() {
        // This command initiates the clean LibGDX shutdown process (calls dispose() on the ApplicationListener)
        System.out.println("--- APPLICATION EXIT REQUESTED ---");
        Gdx.app.exit();
    }
}
