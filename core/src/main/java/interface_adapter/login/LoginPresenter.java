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

    @Override
    public void loginSuccess(LoginOutputData data) {
        // Update the login status
        viewModel.setSuccessfulLogin(true);
        viewModel.setErrorMessage(null);
        viewModel.setLoginEmail(data.getEmail());

        // Load player's data
        PlayerSession session = null;

        if (loadPlayerDataInteractor != null) {
            session = loadPlayerDataInteractor.load(data.getUid(), data.getEmail());
            if (session == null) {
                System.out.println("WARNING: Failed to load player session");
                return;
            }
        }
        viewModel.setPlayerSession(session);

        System.out.println("Login success = " + viewModel.isSuccessfulLogin());
        System.out.println("Error message = " + viewModel.getErrorMessage());
        System.out.println("Email = " + viewModel.getEmail());
    }

    @Override
    public void loginFailed(String error_message) {
        viewModel.setSuccessfulLogin(false);
        viewModel.setErrorMessage(error_message);
    }
}
