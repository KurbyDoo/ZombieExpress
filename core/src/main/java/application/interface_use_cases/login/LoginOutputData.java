/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Output Data Pattern (DTO): Transfers login result to presenter.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data transfer object.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds login output data.
 * - [PASS] Immutable data class (final fields).
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Javadoc present but brief.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] More detailed Javadoc would be helpful.
 */
package application.interface_use_cases.login;

/**
 * pack the successful login data and pass them to presenter
 */

public class LoginOutputData {
    private final String uid;
    private final String email;

    public LoginOutputData(String email, String uid) {
        this.email = email;
        this.uid = uid;
    }
    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }
}
