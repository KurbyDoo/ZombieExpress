package interface_adapter.register;

import application.interface_use_cases.register.RegisterInputBoundary;

/**
 * Controller for the register use case
 *
 * This class acts as the entry point from the UI layer
 * Receiving raw users' input and translate UI actions into use case invocations
 *
 * All validation and business decisions are handled inside the RegisterInteractor
 */

public class RegisterController {

    private final RegisterInputBoundary interactor;

    /**
     * Constructs the controller with the given registerInputBoundary
     * @param interactor
     */
    public RegisterController(RegisterInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Triggers the registration use case with the provided user credentials
     * @param email
     * @param password
     * @param confirmPassword
     */
    public void register(String email, String password, String confirmPassword) {
        interactor.register(email, password, confirmPassword);
    }
}
