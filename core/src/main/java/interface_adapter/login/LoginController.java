/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Interface Adapter (Level 3 - Controllers)
 *
 * DESIGN PATTERNS:
 * - Controller Pattern: Receives input and delegates to use case.
 * - Part of MVC/MVP pattern in Clean Architecture.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] Only imports from application layer (InputBoundary).
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Correctly delegates to use case without business logic.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - input handling only.
 * - [PASS] DIP: Depends on LoginInputBoundary abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Good Javadoc documentation present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Well-documented controller.
 */
package interface_adapter.login;
import application.interface_use_cases.login.LoginInputBoundary;

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
