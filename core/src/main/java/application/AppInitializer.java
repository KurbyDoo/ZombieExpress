package application;

import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import application.interface_use_cases.player_data.PlayerDataAccessInterface;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import data_access.firebase.FirebasePlayerDataAccess;
import presentation.view.ViewFactory;
import presentation.view.ViewManager;


public class AppInitializer {

    private final FirebaseLoginRegisterDataAccess firebaseAuth;
    private final LoadPlayerDataInteractor loadPlayer;
    private final PlayerDataAccessInterface dataAccess;

    public AppInitializer() {
        String apiKey = System.getenv("FIREBASE_API_KEY");

        firebaseAuth = new FirebaseLoginRegisterDataAccess(apiKey);
        dataAccess = new FirebasePlayerDataAccess();
        loadPlayer = new LoadPlayerDataInteractor(dataAccess);

    }

    public ViewManager buildViewManager() {
        ViewFactory.init(firebaseAuth, loadPlayer, dataAccess);
        return new ViewManager();
    }
}
