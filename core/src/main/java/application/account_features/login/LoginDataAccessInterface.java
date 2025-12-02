package application.account_features.login;

/**
 * Data access interface for authenticating a user when login
 */
public interface LoginDataAccessInterface {

    /**
     * Attempts to authenticate the user with the given credentials.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return an authentication response or null(if authentication fails)
     */
    String login(String email, String password);
}
