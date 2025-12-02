package framework.view;

import application.account_features.leaderboard.LeaderboardInteractor;
import application.account_features.login.LoginInteractor;
import application.account_features.player_data.LoadPlayerDataInteractor;
import application.account_features.player_data.PlayerDataAccessInterface;
import application.account_features.player_data.SavePlayerDataInteractor;
import application.account_features.register.RegisterInteractor;
import domain.player.PlayerSession;
import framework.data_access.firebase.FirebaseLoginRegisterDataAccess;
import interface_adapter.controllers.LeaderboardController;
import interface_adapter.controllers.LoginController;
import interface_adapter.controllers.RegisterController;
import interface_adapter.presenters.LeaderboardPresenter;
import interface_adapter.presenters.LoginPresenter;
import interface_adapter.presenters.RegisterPresenter;
import interface_adapter.view_models.LeaderboardViewModel;
import interface_adapter.view_models.LoginViewModel;
import interface_adapter.view_models.RegisterViewModel;

public class ViewFactory {

    private static FirebaseLoginRegisterDataAccess firebaseAuth;
    private static LoadPlayerDataInteractor loadPlayer;
    private static PlayerSession currentSession;
    private static PlayerDataAccessInterface playerDataAccess;

    public static void init(FirebaseLoginRegisterDataAccess auth,
                            LoadPlayerDataInteractor loader, PlayerDataAccessInterface dataAccess) {

        firebaseAuth = auth;
        loadPlayer = loader;
        playerDataAccess = dataAccess;
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

    public static LeaderboardView createLeaderboardView(ViewManager vm) {
        LeaderboardViewModel vmLeader = new LeaderboardViewModel();
        LeaderboardPresenter presenter = new LeaderboardPresenter(vmLeader);
        LeaderboardInteractor interactor = new LeaderboardInteractor(playerDataAccess, presenter);
        LeaderboardController controller = new LeaderboardController(interactor, vm);
        return new LeaderboardView(vm, controller, vmLeader);
    }


    public static GameScreen createGameScreen() {
        SavePlayerDataInteractor saveScore = new SavePlayerDataInteractor(playerDataAccess, currentSession);
        return new GameScreen(currentSession, saveScore);
    }

    public static PlayerSession getCurrentSession() {
        return currentSession;
    }

    public static void setCurrentSession(PlayerSession session) {
        currentSession = session;
    }

    public static PlayerDataAccessInterface getPlayerDataAccess() {
        return playerDataAccess;
    }
}
