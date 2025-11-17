package runner;

import UseCases.Login.*;
import UseCases.PlayerData.LoadPlayerDataInteractor;
import UseCases.PlayerData.PlayerDataAccessInterface;
import UseCases.Register.RegisterUserDataAccessInterface;
import data_access.MockLoginRegisterDataAccess;
import data_access.MockPlayerDataAccess;
import interface_adapter.*;
import data_access.FirebaseAuthManager;
import presentation.view.LoginView;

public class testMain{
    public static void main(String[] args) {
    MockLoginRegisterDataAccess mockUserDB = new MockLoginRegisterDataAccess();
    LoginViewModel viewModel = new LoginViewModel();

    PlayerDataAccessInterface playerDAO = new MockPlayerDataAccess();
    LoadPlayerDataInteractor loadPlayer = new LoadPlayerDataInteractor(playerDAO);

    LoginPresenter presenter = new LoginPresenter(viewModel, loadPlayer);
    LoginDataAccessInterface loginDAO = mockUserDB;
    LoginInteractor loginInteractor = new LoginInteractor(loginDAO, presenter);
    LoginController loginController = new LoginController(loginInteractor);
    new LoginView(loginController, viewModel,mockUserDB, loadPlayer);
    }
}
