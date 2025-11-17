package interface_adapter.login;

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
        viewModel.setErrorMessage(null);
        viewModel.setLoginEmail(data.getEmail());

        if (loadPlayerDataInteractor != null) {
            PlayerSession session = loadPlayerDataInteractor.load(data.getUid(), data.getEmail());
            System.out.println("Loaded UID = " + session.getUid());
            System.out.println("Highest score = " + session.getHeightScore());
        }

        System.out.println("success = " + viewModel.isSuccessfulLogin());
        System.out.println("error = " + viewModel.getErrorMessage());
        System.out.println("email = " + viewModel.getEmail());
        PlayerSession session = loadPlayerDataInteractor.load(data.getUid(), data.getEmail());
        System.out.println("Loaded UID = " + session.getUid());
        System.out.println("Highest score = " + session.getHeightScore());
    }

    @Override
    public void loginFailed(String error_message) {
        viewModel.setSuccessfulLogin(false);
        viewModel.setErrorMessage(error_message);
    }
}
