/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Entry Point/Runner)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package runner;

import application.interface_use_cases.login.LoginInteractor;
import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import data_access.firebase.FirebaseInitializer;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import data_access.firebase.FirebasePlayerDataAccess;
import interface_adapter.login.*;

public class testLoginFlow {
    public static void main(String[] args) {
        FirebaseInitializer.init();
        String apiKey = System.getenv("FIREBASE_API_KEY");
        System.out.println("MAIN DEBUG: API KEY = " + apiKey);

        FirebaseLoginRegisterDataAccess authDataAccess = new FirebaseLoginRegisterDataAccess(apiKey);
        FirebasePlayerDataAccess playerDataAccess = new FirebasePlayerDataAccess();

        LoadPlayerDataInteractor loadPlayer = new LoadPlayerDataInteractor(playerDataAccess);
        LoginViewModel viewModel = new LoginViewModel();
        LoginPresenter presenter = new LoginPresenter(viewModel, loadPlayer);
        LoginInteractor interactor = new LoginInteractor(authDataAccess, presenter);
        LoginController controller = new LoginController(interactor);

        viewModel.addPropertyChangeListener(evt ->{
            System.out.println("EVENT: " + evt.getPropertyName());

            if ("playerSession".equals(evt.getPropertyName())) {
                System.out.println(">>> Player session loaded:");
                System.out.println(evt.getNewValue());
            }
        });
        System.out.println("Testing login ...");
        controller.login("The email address you have registered", "password");
        try{
            Thread.sleep(2000);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done.");



    }
}
