/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Port Pattern (Data Access Interface): Defines abstraction for login data access.
 * - Repository Pattern: Abstraction for authentication storage.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure port interface - EXCELLENT Clean Architecture.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - authentication contract.
 * - [PASS] ISP: Focused interface with single method.
 * - [PASS] DIP: Use cases depend on this abstraction.
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
 * Data access interface for authenticating a user when login
 */
public interface LoginDataAccessInterface {

    /**
     * Attempts to authenticate the user with the given credentials.
     *
     * @param email the user's email
     * @param password the user's password
     * @return an authentication response or null(if authentication fails)
     */
    String login(String email, String password);
}
