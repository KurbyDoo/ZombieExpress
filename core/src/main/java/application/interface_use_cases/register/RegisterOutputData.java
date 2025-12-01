/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Output Data Pattern (DTO): Transfers registration result to presenter.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data transfer object.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Holds registration output data.
 * - [PASS] Immutable data class (final fields).
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Javadoc present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Properly documented.
 */
package application.interface_use_cases.register;

/**
 * pack the successful register data and pass them to presenter
 */
public class RegisterOutputData {
    private final String email;
    private final String uid;

    public RegisterOutputData(String email, String uid) {
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
