package application.account_features.login;

/**
 * Output boundary for the Login use case
 * <p>
 * This interface defines how the LoginInteractor communicates the result
 * of the login process to the presenter.
 * <p>
 * Describing only the success and failure outcomes
 */
public interface LoginOutputBoundary {

    /**
     * Called when the login process completes successfully
     *
     * @param data the output data containing the user's email and UID
     */
    void loginSuccess(LoginOutputData data);

    /**
     * Called when the login process fails
     *
     * @param error_message
     */
    void loginFailed(String error_message);
}
