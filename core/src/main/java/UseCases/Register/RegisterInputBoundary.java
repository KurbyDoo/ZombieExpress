package UseCases.Register;

/**
 * Input boundary for the Register use case
 *
 * This interface defines how the UI layer via a controller
 * initiates the registration process
 */
public interface RegisterInputBoundary {

    /**
     * Executes the registration use case with the provided credentials
     * @param username
     * @param password
     * @param confirmPassword
     */
    void register(String username, String password, String confirmPassword);
}
