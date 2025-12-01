/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Port Pattern (Data Access Interface): Defines abstraction for user registration.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure port interface.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: User creation contract.
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
package application.interface_use_cases.register;

/**
 * Data access interface for creating a new user during the registration process
 *
 * Implementations of this interface are responsible for attempting to create a
 * new user account with the provided credentials and returning a unique user ID
 * on success, or null on failure
 */
public interface RegisterUserDataAccessInterface {

    /**
     * Attempts to create a new user with the given username and password
     * @param username
     * @param password
     * @return a unique UID if creation is successful, or null if creation fails
     */
    String newUser(String username, String password);
}
