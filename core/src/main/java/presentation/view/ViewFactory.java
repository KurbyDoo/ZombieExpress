package presentation.view;

import application.interface_use_cases.login.LoginInteractor;
import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import application.interface_use_cases.register.RegisterInteractor;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.register.RegisterController;
import interface_adapter.register.RegisterPresenter;
import interface_adapter.register.RegisterViewModel;

import javax.swing.*;

public class ViewFactory {
    private static FirebaseLoginRegisterDataAccess firebaseAuthSingleton;
    private static LoadPlayerDataInteractor loadPlayerSingleton;

    public static void init(FirebaseLoginRegisterDataAccess firebaseAuth, LoadPlayerDataInteractor loadPlayer) {
        firebaseAuthSingleton = firebaseAuth;
        loadPlayerSingleton = loadPlayer;

    }

    public static JFrame createLoginView(ViewManager viewManager) {
       LoginViewModel loginViewModel = new LoginViewModel();
       LoginPresenter loginPresenter = new LoginPresenter(loginViewModel, loadPlayerSingleton);
       LoginInteractor loginInteractor = new LoginInteractor(firebaseAuthSingleton, loginPresenter);
       LoginController loginController = new LoginController(loginInteractor);
       return new LoginView(
           viewManager,loginController,loginViewModel,firebaseAuthSingleton,loadPlayerSingleton
       );
    }
    public static JFrame createRegisterView(ViewManager viewManager) {
        RegisterViewModel registerViewModel = new RegisterViewModel();
        RegisterPresenter registerPresenter = new RegisterPresenter(registerViewModel);
        RegisterInteractor registerInteractor = new RegisterInteractor(firebaseAuthSingleton, registerPresenter);
        RegisterController registerController = new RegisterController(registerInteractor);
        return new RegisterView(
            viewManager,registerController,registerViewModel,firebaseAuthSingleton,loadPlayerSingleton
        );
    }
}

