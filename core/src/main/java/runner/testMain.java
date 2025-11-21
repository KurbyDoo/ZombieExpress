package runner;

import UseCases.Login.*;
import UseCases.PlayerData.LoadPlayerDataInteractor;
import data_access.firebase.FirebaseInitializer;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import data_access.firebase.FirebasePlayerDataAccess;
import interface_adapter.login.*;
import presentation.view.LoginView;

public class testMain {

    public static void main(String[] args) {

        // initialize ui
        FirebaseInitializer.init();

        // load api key
        String apiKey = System.getenv("FIREBASE_API_KEY");
        if (apiKey == null) {
            System.err.println("ERROR: FIREBASE_API_KEY is missing.");
            return;
        }

        // 3. initialize DataAccess
        FirebaseLoginRegisterDataAccess firebaseAuth = new FirebaseLoginRegisterDataAccess(apiKey);
        FirebasePlayerDataAccess playerDataAccess = new FirebasePlayerDataAccess();

        // UseCase Interactor
        LoadPlayerDataInteractor loadPlayer = new LoadPlayerDataInteractor(playerDataAccess);

        // Login Presenter + ViewModel + Controller
        LoginViewModel loginVM = new LoginViewModel();
        LoginPresenter loginPresenter = new LoginPresenter(loginVM, loadPlayer);
        LoginInteractor loginInteractor = new LoginInteractor(firebaseAuth, loginPresenter);
        LoginController loginController = new LoginController(loginInteractor);

        // UI
        new LoginView(loginController, loginVM, firebaseAuth, loadPlayer);

        System.out.println("UI Started.");
    }
}

//package runner;
//
//import UseCases.Login.*;
//import UseCases.PlayerData.LoadPlayerDataInteractor;
//import UseCases.PlayerData.PlayerDataAccessInterface;
//import data_access.login.MockLoginRegisterDataAccess;
//import data_access.player.MockPlayerDataAccess;
//import interface_adapter.login.LoginController;
//import interface_adapter.login.LoginPresenter;
//import interface_adapter.login.LoginViewModel;
//import presentation.view.LoginView;
//
//public class testMain{
//    public static void main(String[] args) {
//    MockLoginRegisterDataAccess mockUserDB = new MockLoginRegisterDataAccess();
//    LoginViewModel viewModel = new LoginViewModel();
//
//    PlayerDataAccessInterface playerDAO = new MockPlayerDataAccess();
//    LoadPlayerDataInteractor loadPlayer = new LoadPlayerDataInteractor(playerDAO);
//
//    LoginPresenter presenter = new LoginPresenter(viewModel, loadPlayer);
//    LoginDataAccessInterface loginDAO = mockUserDB;
//    LoginInteractor loginInteractor = new LoginInteractor(loginDAO, presenter);
//    LoginController loginController = new LoginController(loginInteractor);
//    new LoginView(loginController, viewModel,mockUserDB, loadPlayer);
//    }
//}


