/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Use Case Pattern: Implements application exit logic.
 * - Port Pattern: Uses ApplicationLifecyclePort for framework abstraction.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers (only uses ports).
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Uses ApplicationLifecyclePort abstraction correctly.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - handles game exit.
 * - [PASS] DIP: Depends on ApplicationLifecyclePort abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Good Javadoc documentation present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Well-documented class.
 */
package application.game_use_cases.exit_game;

import application.game_use_cases.ports.ApplicationLifecyclePort;

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
