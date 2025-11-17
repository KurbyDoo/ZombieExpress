package interface_adapter;

import UseCases.Register.RegisterInputBoundary;

public class RegisterController {

    private final RegisterInputBoundary interactor;

    public RegisterController(RegisterInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void register(String email, String password, String confirmPassword) {
        interactor.register(email, password, confirmPassword);
    }
}
