package interface_adapter;

import UseCases.Register.RegisterOutputBoundary;
import UseCases.Register.RegisterOutputData;

public class RegisterPresenter implements RegisterOutputBoundary {
    private final RegisterViewModel viewModel;

    public  RegisterPresenter(RegisterViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void registerSuccess(RegisterOutputData data){
        viewModel.setRegisterSuccess(true);
        viewModel.setErrorMessage(null);
    }

    public void registerFail(String errorMessage){
        viewModel.setRegisterSuccess(false);
        viewModel.setErrorMessage(errorMessage);
    }
}
