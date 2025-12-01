package application;

import application.account_features.player_data.LoadPlayerDataInteractor;
import framework.data_access.firebase.FirebaseLoginRegisterDataAccess;
import framework.data_access.firebase.FirebasePlayerDataAccess;
import framework.view.ViewFactory;
import framework.view.ViewManager;


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
