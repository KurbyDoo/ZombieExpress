package interface_adapter.login;
import UseCases.Login.LoginInputBoundary;

public class LoginController {
    private final LoginInputBoundary interactor;

    public LoginController(LoginInputBoundary interactor) {
        this.interactor = interactor;
    }
    public void login(String email, String password) {
        interactor.login(email, password);
    }
}
