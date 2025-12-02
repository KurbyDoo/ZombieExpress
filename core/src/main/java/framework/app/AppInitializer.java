package framework.app;

import application.account_features.player_data.LoadPlayerDataInteractor;
import application.account_features.player_data.PlayerDataAccessInterface;
import framework.data_access.firebase.FirebaseLoginRegisterDataAccess;
import framework.data_access.firebase.FirebasePlayerDataAccess;
import framework.view.ViewFactory;
import framework.view.ViewManager;

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
