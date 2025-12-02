package interface_adapter.presenters;

import application.account_features.login.LoginOutputBoundary;
import application.account_features.login.LoginOutputData;
import application.account_features.player_data.LoadPlayerDataInteractor;
import domain.player.PlayerSession;
import framework.view.ViewFactory;
import interface_adapter.view_models.LoginViewModel;

/**
 * Presenter for the Login use case
 * <p>
 * This class implements the LoginOutputBoundary and acts as the output
 * adapter between the LoginInteractor(UseCase layer) and the ViewModel/UI
 * (interface adapter layer)
 * <p>
 * Responsibilities:
 * - Receive success or failure result from the LoginInteractor
 * - Format and populate the LoginViewModel with UI-ready data
 * - Optionally load additional player data upon successful login
 * <p>
 * The presenter contains no business logic and dont perform UI rendering
 */
public class LoginPresenter implements LoginOutputBoundary {
    private final LoginViewModel viewModel;
    private final LoadPlayerDataInteractor loadPlayerDataInteractor;

    public LoginPresenter(LoginViewModel viewModel,
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

        // Load player's data
        PlayerSession session = loadPlayerDataInteractor.load(data.getEmail(), data.getUid());
        if (session == null) {
            System.out.println("WARNING: Failed to load player session");
            return;
        }
        ViewFactory.setCurrentSession(session);
        viewModel.setPlayerSession(session);

        System.out.println("Login success = " + viewModel.isSuccessfulLogin());
        System.out.println("Error message = " + viewModel.getErrorMessage());
        System.out.println("Email = " + viewModel.getEmail());
    }

    /**
     * Handles a failed login attempt by storing error message in the ViewModel
     *
     * @param errorMessage
     */
    @Override
    public void loginFailed(String errorMessage) {
        viewModel.setSuccessfulLogin(false);
        viewModel.setErrorMessage(errorMessage);
    }
}
