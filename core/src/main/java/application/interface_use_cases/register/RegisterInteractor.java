/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Interactor Pattern: Implements registration use case.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Handles registration logic.
 * - [PASS] LSP: Correctly implements RegisterInputBoundary.
 * - [PASS] DIP: Uses data access and output boundary abstractions.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Missing space before '{' on line 12.
 * - [PASS] Good Javadoc documentation present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Missing space before opening brace.
 */
package application.interface_use_cases.register;

/**
 * Interactor for the Register use case
 *
 * It validates the provided registration data, interacts with the data access layer
 * to create the user account, and reports the outcome to the presenter through the
 * RegisterOutputBoundary
 *
 */

public class RegisterInteractor implements RegisterInputBoundary{
    private final RegisterUserDataAccessInterface dataAccess;
    private final RegisterOutputBoundary presenter;

    public RegisterInteractor(RegisterUserDataAccessInterface dataAccess, RegisterOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    /**
     * Executes the registration use case.
     *
     * Steps:
     *  1. Validate that password and confirmPassword match
     *  2. Attempt user creation through the data access layer
     *  3. Notify presenter of failure or success
     *
     * @param email            the user's email for registration
     * @param password         the user's chosen password
     * @param confirmPassword  password repeated for consistency check
     */
    @Override
    public void register(String email, String password, String confirmPassword){
        if (!password.equals(confirmPassword)){
            presenter.registerFail("Passwords do not match");
            return;
        }

        // else create a new user
        String uid = dataAccess.newUser(email, password);

        if (uid == null){
            presenter.registerFail("Email is invalid");
            return;
        }

        presenter.registerSuccess(new RegisterOutputData(email, uid));
    }
}
