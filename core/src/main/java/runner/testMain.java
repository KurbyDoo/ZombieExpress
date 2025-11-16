package runner;

import UseCases.Login.*;
import UseCases.PlayerData.LoadPlayerDataInteractor;
import UseCases.PlayerData.PlayerDataAccessInterface;
import data_access.MockPlayerDataAccess;
import interface_adapter.*;
import data_access.FirebaseAuthManager;
import presentation.view.LoginView;

public class testMain{
    public static void main(String[] args) {
    LoginViewModel viewModel = new LoginViewModel();
    PlayerDataAccessInterface playerDAO = new MockPlayerDataAccess();
    LoadPlayerDataInteractor loadPlayer = new LoadPlayerDataInteractor(playerDAO);
    LoginPresenter presenter = new LoginPresenter(viewModel, loadPlayer);
    LoginDataAccessInterface dataAccess = new FirebaseAuthManager();
    LoginInputBoundary interactor = new LoginInteractor(dataAccess, presenter);
    LoginController controller = new LoginController(interactor);
    new LoginView(controller, viewModel);
    }
}
