package application.account_features.login;

/**
 *  Input boundary for the Login use case
 *  Defines the method that the UI / controller must call to initiate the login process
 */
public interface LoginInputBoundary {

    /**
     * Executes the login use case with the provided email and password.
     *
     * @param email the user's email
     * @param password the user's password
     */
    void login(String email, String password);
}
