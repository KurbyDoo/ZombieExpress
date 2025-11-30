package application;

import application.interface_use_cases.login.LoginInteractor;
import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import data_access.firebase.FirebasePlayerDataAccess;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import presentation.view.LoginView;

public class AppInitializer {

    private final FirebaseLoginRegisterDataAccess firebaseAuth;
    private final LoadPlayerDataInteractor loadPlayer;

    public AppInitializer() {
        String apiKey = System.getenv("FIREBASE_API_KEY");
        firebaseAuth = new FirebaseLoginRegisterDataAccess(apiKey);
        loadPlayer = new LoadPlayerDataInteractor(new FirebasePlayerDataAccess());
    }

    public LoginView buildLoginView() {
        LoginViewModel loginVM = new LoginViewModel();
        LoginPresenter presenter = new LoginPresenter(loginVM, loadPlayer);
        LoginInteractor interactor = new LoginInteractor(firebaseAuth, presenter);
        LoginController controller = new LoginController(interactor);

        return new LoginView(controller, loginVM, firebaseAuth, loadPlayer);
    }
}
