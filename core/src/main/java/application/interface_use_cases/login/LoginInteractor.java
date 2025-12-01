/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Interactor Pattern: Implements login use case business logic.
 * - Uses Output Boundary for presenter communication.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [WARN] Imports com.google.gson classes for JSON parsing.
 *   While Gson is a common library, parsing logic could be moved to
 *   the data access layer to keep use cases pure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - handles login logic.
 * - [PASS] LSP: Correctly implements LoginInputBoundary.
 * - [PASS] DIP: Uses data access and output boundary abstractions.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Good Javadoc documentation present.
 * - [WARN] Typo in Javadoc: "sucdcess" should be "success".
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Typo in Javadoc comment.
 * - [PASS] Generally well-documented.
 */
package application.interface_use_cases.login;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Interactor for the login use case
 *
 * Implements the core business logic for authenticating a user:
 * - Validates credentials
 * - Calls the authentication data-access interface
 * - Parses returned authentication data
 * - Notifies the presenter of sucdcess login or failure
 */
public class LoginInteractor implements LoginInputBoundary {

    /**
     * Attempts to authenticate a user given email and password
     */
    private final LoginDataAccessInterface userDataAccess;
    private final LoginOutputBoundary presenter;

    public LoginInteractor(LoginDataAccessInterface userDataAccess, LoginOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }
    @Override
    public void login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()){
            presenter.loginFailed("Email or password is empty");
            return;
        }// blank input
        String json = userDataAccess.login(email, password);// Call DataAccess to log in to Firebase

        // Judge whether the login failed
        if (json == null){
            presenter.loginFailed("Invalid email or password");
            return;
        }
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        String uid = obj.get("localId").getAsString();
        LoginOutputData data = new LoginOutputData(email, uid);
        presenter.loginSuccess(data);
        // login successfully, call presenter handle success case
    }
}
