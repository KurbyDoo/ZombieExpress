package application.game_features.exit_game;

import application.ports.ApplicationLifecyclePort;

/**
 * Application Use Case responsible for executing the clean exit sequence.
 * It coordinates any necessary pre-exit logic (like saving) and delegates the
 * final framework shutdown command to the ApplicationLifecyclePort.
 */
public class ExitGameUseCase { // <-- Renamed back to UseCase

    private final ApplicationLifecyclePort lifecyclePort;

    public ExitGameUseCase(ApplicationLifecyclePort lifecyclePort) { // <-- Updated constructor name
        this.lifecyclePort = lifecyclePort;
    }

    public void execute() {
        System.out.println("Saving state before exit...");

        lifecyclePort.closeApplication();
    }
}
