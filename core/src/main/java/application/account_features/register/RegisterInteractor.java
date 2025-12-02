package application.account_features.register;

/**
 * Interactor for the Register use case
 * <p>
 * It validates the provided registration data, interacts with the data access layer
 * to create the user account, and reports the outcome to the presenter through the
 * RegisterOutputBoundary
 */

public class RegisterInteractor implements RegisterInputBoundary {
    private final RegisterUserDataAccessInterface dataAccess;
    private final RegisterOutputBoundary presenter;

    public RegisterInteractor(RegisterUserDataAccessInterface dataAccess, RegisterOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    /**
     * Executes the registration use case.
     * <p>
     * Steps:
     * 1. Validate that password and confirmPassword match
     * 2. Attempt user creation through the data access layer
     * 3. Notify presenter of failure or success
     *
     * @param email           the user's email for registration
     * @param password        the user's chosen password
     * @param confirmPassword password repeated for consistency check
     */
    @Override
    public void register(String email, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            presenter.registerFail("Passwords do not match");
            return;
        }

        if (email == null || !email.contains("@") || !email.contains(".")) {
            presenter.registerFail("Invalid email format.Your email format should contains both of @ and .");
            return;
        }

        // need to check the length of the password
        if (password.length() < 6) {
            presenter.registerFail("Password must be at least 6 characters.");
            return;
        }


        // else create a new user
        String uid = dataAccess.newUser(email, password);

        if (uid == null) {
            presenter.registerFail("Email is invalid");
            return;
        }

        presenter.registerSuccess(new RegisterOutputData(email, uid));
    }
}
