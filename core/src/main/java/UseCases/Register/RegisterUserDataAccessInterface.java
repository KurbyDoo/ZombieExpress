package UseCases.Register;

/**
 * Data access interface for creating a new user during the registration process
 *
 * Implementations of this interface are responsible for attempting to create a
 * new user account with the provided credentials and returning a unique user ID
 * on success, or null on failure
 */
public interface RegisterUserDataAccessInterface {

    /**
     * Attempts to create a new user with the given username and password
     * @param username
     * @param password
     * @return a unique UID if creation is successful, or null if creation fails
     */
    String newUser(String username, String password);
}
