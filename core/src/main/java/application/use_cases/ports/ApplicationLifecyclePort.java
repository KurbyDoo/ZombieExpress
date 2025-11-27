package application.use_cases.ports;

/**
 * Port interface defining system-level application operations required by Use Cases.
 * This decouples the Application Layer from the underlying framework (e.g., LibGDX).
 */
public interface ApplicationLifecyclePort {
    /**
     * Initiates the shutdown sequence for the application.
     */
    void closeApplication();
}
