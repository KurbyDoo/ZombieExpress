/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Interface Adapter (Level 3 - Controllers)
 *
 * DESIGN PATTERNS:
 * - Controller Pattern: Receives input and delegates to use case.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] Only imports from application layer.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Correctly delegates to use case.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - input handling.
 * - [PASS] DIP: Depends on RegisterInputBoundary abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package interface_adapter.register;

import application.interface_use_cases.register.RegisterInputBoundary;

/**
 * Controller for the register use case
 *
 * This class acts as the entry point from the UI layer
 * Receiving raw users' input and translate UI actions into use case invocations
 *
 * All validation and business decisions are handled inside the RegisterInteractor
 */

public class RegisterController {

    private final RegisterInputBoundary interactor;

    /**
     * Constructs the controller with the given registerInputBoundary
     * @param interactor
     */
    public RegisterController(RegisterInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Triggers the registration use case with the provided user credentials
     * @param email
     * @param password
     * @param confirmPassword
     */
    public void register(String email, String password, String confirmPassword) {
        interactor.register(email, password, confirmPassword);
    }
}
