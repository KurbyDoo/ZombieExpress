/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Output Boundary Pattern: Defines presenter contract for registration results.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case interface.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Defines result notification contract.
 * - [PASS] ISP: Focused interface (success/failure methods).
 * - [PASS] DIP: Presenters implement this abstraction.
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
 * Output boundary for the Register use case
 *
 * This interface defines how the RegisterInteractor communicates the result
 * of the register process to the presenter
 *
 * Describing only the success and failure outcomes
 */
public interface RegisterOutputBoundary {

    /**
     * Called when the register process completes successfully
     * @param data the output data containing the user's email and UID
     */
    void registerSuccess(RegisterOutputData data);

    /**
     * Called when the register process fails
     * @param errorMessage
     */
    void registerFail(String errorMessage);
}
