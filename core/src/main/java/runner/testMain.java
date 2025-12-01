package runner;

import application.interface_use_cases.login.LoginInteractor;
import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import data_access.firebase.FirebaseInitializer;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import data_access.firebase.FirebasePlayerDataAccess;
import interface_adapter.login.*;
import interface_adapter.view.LoginView;

/**
 * This is the Application entry point.
 * This class wires together Firebase initialization, Data Access objects,
 * Use Case interactors, presenters, controllers, and finally launches the UI.
 */
public class testMain {

    /**
     * Initializes Firebase, loads configuration, constructs all Clean Architecture layers,
     * and launches the Login UI.
     */
    public static void main(String[] args) {
        // initialize Firebase Admin SDK (loads services account credentials)
        FirebaseInitializer.init();

        // load api key from environment variable for REST login requests
        String apiKey = System.getenv("FIREBASE_API_KEY");
        if (apiKey == null) {
            System.err.println("ERROR: FIREBASE_API_KEY is missing. PLEASE configure your FIREBASE_API_KEY and try again.");
            return;
        }

        // This is the  DataAccess layer
        // - firebaseAuth: handles login and registration (REST API and Admin SDK)
        // - playerDataAccess: handles loading and saving player session info(Firestore)
        FirebaseLoginRegisterDataAccess firebaseAuth = new FirebaseLoginRegisterDataAccess(apiKey);
        FirebasePlayerDataAccess playerDataAccess = new FirebasePlayerDataAccess();

        // UseCase Interactor uses Data Access to read/store player data.
        LoadPlayerDataInteractor loadPlayer = new LoadPlayerDataInteractor(playerDataAccess);

        // layer: ViewModel → Presenter → Interactor → DataAccess
        LoginViewModel loginVM = new LoginViewModel();
        LoginPresenter loginPresenter = new LoginPresenter(loginVM, loadPlayer);
        LoginInteractor loginInteractor = new LoginInteractor(firebaseAuth, loginPresenter);
        LoginController loginController = new LoginController(loginInteractor);

        // Launch login UI with dependency-injected controller and view model
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


