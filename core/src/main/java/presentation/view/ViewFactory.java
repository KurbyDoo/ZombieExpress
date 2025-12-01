/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Presentation/Views)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package presentation.view;

import application.interface_use_cases.login.LoginInteractor;
import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import application.interface_use_cases.register.RegisterInteractor;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import interface_adapter.login.*;
import interface_adapter.register.*;

public class ViewFactory {

    private static FirebaseLoginRegisterDataAccess firebaseAuth;
    private static LoadPlayerDataInteractor loadPlayer;

    public static void init(FirebaseLoginRegisterDataAccess auth,
                            LoadPlayerDataInteractor loader) {

        firebaseAuth = auth;
        loadPlayer = loader;
    }

    public static LoginView createLoginView(ViewManager vm) {
        LoginViewModel vmLogin = new LoginViewModel();
        LoginPresenter presenter = new LoginPresenter(vmLogin, loadPlayer);
        LoginInteractor interactor = new LoginInteractor(firebaseAuth, presenter);
        LoginController controller = new LoginController(interactor);
        return new LoginView(vm, controller, vmLogin);
    }

    public static RegisterView createRegisterView(ViewManager vm) {
        RegisterViewModel vmRegister = new RegisterViewModel();
        RegisterPresenter presenter = new RegisterPresenter(vmRegister);
        RegisterInteractor interactor = new RegisterInteractor(firebaseAuth, presenter);
        RegisterController controller = new RegisterController(interactor);
        return new RegisterView(vm, controller, vmRegister);
    }
}
