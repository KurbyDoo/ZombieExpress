/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Boundary Pattern: Defines contract for login use case.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case interface.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines login contract.
 * - [PASS] ISP: Focused interface with single method.
 * - [PASS] DIP: Controllers depend on this abstraction.
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
 *  Input boundary for the Login use case
 *  Defines the method that the UI / controller must call to initiate the login process
 */
public interface LoginInputBoundary {

    /**
     * Executes the login use case with the provided email and password.
     *
     * @param email the user's email
     * @param password the user's password
     */
    void login(String email, String password);
}
