package interface_adapter.login;

import application.interface_use_cases.login.LoginOutputBoundary;
import application.interface_use_cases.login.LoginOutputData;
import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import domain.player.PlayerSession;

/**
 * Presenter for the Login use case
 *
 * This class implements the LoginOutputBoundary and acts as the output
 * adapter between the LoginInteractor(UseCase layer) and the ViewModel/UI
 * (interface adapter layer)
 *
 * Responsibilities:
 * - Receive success or failure result from the LoginInteractor
 * - Format and populate the LoginViewModel with UI-ready data
 * - Optionally load additional player data upon successful login
 *
 * The presenter contains no business logic and dont perform UI rendering
 */
public class LoginPresenter implements LoginOutputBoundary {
    private final LoginViewModel viewModel;
    private final LoadPlayerDataInteractor loadPlayerDataInteractor;

    public LoginPresenter(LoginViewModel  viewModel,
                          LoadPlayerDataInteractor loadPlayerDataInteractor) {
        this.viewModel = viewModel;
        this.loadPlayerDataInteractor = loadPlayerDataInteractor;
    }

    /**
     * Handles a successful login attempt by updating the ViewModel
     * and loading the user's playerSession
     *
     * @param data the output data containing the user's email and UID
     */
    @Override
    public void loginSuccess(LoginOutputData data) {
        // Update the login status
        viewModel.setSuccessfulLogin(true);
        viewModel.setErrorMessage(null);
        viewModel.setLoginEmail(data.getEmail());
        viewModel.setShouldStartGame(true);

        // Load player's data
        PlayerSession session = null;

        if (loadPlayerDataInteractor != null) {
            session = loadPlayerDataInteractor.load(data.getEmail(), data.getUid());
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

    /**
     * Handles a failed login attempt by storing error message in the ViewModel
     * @param errorMessage
     */
    @Override
    public void loginFailed(String errorMessage) {
        viewModel.setSuccessfulLogin(false);
        viewModel.setErrorMessage(errorMessage);
    }
}
