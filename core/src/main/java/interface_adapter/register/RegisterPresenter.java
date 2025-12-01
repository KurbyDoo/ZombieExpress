/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Interface Adapter (Level 3 - Presenters)
 *
 * DESIGN PATTERNS:
 * - Presenter Pattern: Converts use case output to ViewModel.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] Imports from application layer.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Correctly implements OutputBoundary.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - output formatting.
 * - [PASS] LSP: Implements RegisterOutputBoundary correctly.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package interface_adapter.register;

import application.interface_use_cases.register.RegisterOutputBoundary;
import application.interface_use_cases.register.RegisterOutputData;

/**
 * Presenter for the Register use case
 *
 * This class implements the RegisterOutputBoundary and acts as the output
 * adapter between the RegisterInteractor(UseCase layer) and the ViewModel/UI
 * (interface adapter layer)
 *
 * It converts the result of the registration process into UI-ready
 * data by updating the ViewModel's status and error message fields
 */
public class RegisterPresenter implements RegisterOutputBoundary {
    private final RegisterViewModel viewModel;

    public  RegisterPresenter(RegisterViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Handles a successful registration & updating the view model
     * @param data
     */
    @Override
    public void registerSuccess(RegisterOutputData data){
        viewModel.setRegisterSuccess(true);
        viewModel.setErrorMessage(null);
    }

    /**
     * Handles a failed registration attempt & updating
     * @param errorMessage
     */
    public void registerFail(String errorMessage){
        viewModel.setRegisterSuccess(false);
        viewModel.setErrorMessage(errorMessage);
    }
}
