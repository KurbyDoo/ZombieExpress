package application;

import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import data_access.firebase.FirebasePlayerDataAccess;
import presentation.view.ViewFactory;
import presentation.view.ViewManager;


public class AppInitializer {

    private final FirebaseLoginRegisterDataAccess firebaseAuth;
    private final LoadPlayerDataInteractor loadPlayer;

    public AppInitializer() {
        String apiKey = System.getenv("FIREBASE_API_KEY");

        firebaseAuth = new FirebaseLoginRegisterDataAccess(apiKey);
        loadPlayer = new LoadPlayerDataInteractor(new FirebasePlayerDataAccess());
    }

    public ViewManager buildViewManager() {
        ViewFactory.init(firebaseAuth, loadPlayer);
        return new ViewManager();
    }
}
