package interface_adapter;

import UseCases.Login.LoginOutputBoundary;
import UseCases.Login.LoginOutputData;
import UseCases.PlayerData.LoadPlayerDataInteractor;
import domain.entities.PlayerSession;

public class LoginPresenter implements LoginOutputBoundary {
    private final LoginViewModel viewModel;
    private final LoadPlayerDataInteractor loadPlayerDataInteractor;

    public LoginPresenter(LoginViewModel  viewModel, LoadPlayerDataInteractor loadPlayerDataInteractor) {
        this.viewModel = viewModel;
        this.loadPlayerDataInteractor = loadPlayerDataInteractor;
    }


    // TODO: need to set the success and fail status and notify the UI(javaSwing) to update
    @Override
    public void loginSuccess(LoginOutputData data) {
        viewModel.setSuccessfulLogin(true);
        viewModel.setErrorMssage(null);
        viewModel.setLoginEmail(data.getEmail());
        viewModel.firePropertyChange();
        PlayerSession playerSession = loadPlayerDataInteractor.load(data.getUid(), data.getEmail());

        System.out.println("success = " + viewModel.isSuccessfulLogin());
        System.out.println("error = " + viewModel.getErrorMessage());
        System.out.println("email = " + viewModel.getLoginEmail());
        PlayerSession session = loadPlayerDataInteractor.load(data.getUid(), data.getEmail());
        System.out.println("Loaded UID = " + session.getUid());
        System.out.println("Highest score = " + session.getHeightScore());
    }

    @Override
    public void loginFailed(String error_message) {
        viewModel.setSuccessfulLogin(false);
        viewModel.setErrorMssage(error_message);
        viewModel.firePropertyChange();
    }
}
