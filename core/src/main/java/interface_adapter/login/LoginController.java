package interface_adapter.login;
import UseCases.Login.LoginInputBoundary;

/**
 * Controller for the Login use case
 *
 * Acts as the entry point from the UI layer
 * Receives raw user input and forwards it to the LoginInputBoundary (LoginInteractor)
 *
 * contains no business logic
 */
public class LoginController {
    private final LoginInputBoundary interactor;

    public LoginController(LoginInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Triggers the login use case with UI-provided inputs.
     *
     * @param email user email from UI input field
     * @param password user password from UI input field
     */
    public void login(String email, String password) {
        interactor.login(email, password);
    }
}
