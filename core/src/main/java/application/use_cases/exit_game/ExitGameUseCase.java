package application.use_cases.exit_game;

import application.use_cases.ports.ApplicationLifecyclePort;

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
        // Here you can add application-specific logic like saving state.
        System.out.println("Saving state before exit...");

        // Delegate the framework-specific exit command to the port/adapter
        lifecyclePort.closeApplication();
    }
}
