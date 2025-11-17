package runner;

import UseCases.Login.*;
import UseCases.PlayerData.LoadPlayerDataInteractor;
import UseCases.PlayerData.PlayerDataAccessInterface;
import data_access.login.MockLoginRegisterDataAccess;
import data_access.player.MockPlayerDataAccess;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
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
