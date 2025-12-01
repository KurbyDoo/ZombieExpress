/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Output Boundary Pattern: Defines presenter contract for login results.
 * - Observer Pattern: Presenter observes use case outcomes.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case interface.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines result notification contract.
 * - [PASS] ISP: Focused interface (2 methods: success/failure).
 * - [PASS] DIP: Presenters implement this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [PASS] Good Javadoc documentation present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Well-documented interface.
 */
package application.interface_use_cases.login;

/**
 * Output boundary for the Login use case
 *
 * This interface defines how the LoginInteractor communicates the result
 * of the login process to the presenter.
 *
 * Describing only the success and failure outcomes
 */
public interface LoginOutputBoundary {

    /**
     * Called when the login process completes successfully
     * @param data the output data containing the user's email and UID
     */
    void loginSuccess(LoginOutputData data);

    /**
     * Called when the login process fails
     * @param error_message
     */
    void loginFailed(String error_message);
}
