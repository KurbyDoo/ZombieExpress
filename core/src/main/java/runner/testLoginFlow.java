package runner;

import UseCases.Login.*;
import UseCases.PlayerData.LoadPlayerDataInteractor;
import data_access.Firebase.FirebaseInitializer;
import data_access.Firebase.FirebaseLoginRegisterDataAccess;
import data_access.Firebase.FirebasePlayerDataAccess;
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
