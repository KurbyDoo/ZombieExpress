package interface_adapter.register;

import application.account_features.register.RegisterOutputBoundary;
import application.account_features.register.RegisterOutputData;

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
