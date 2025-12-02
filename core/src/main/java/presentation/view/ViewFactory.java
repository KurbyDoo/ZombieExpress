package presentation.view;

import application.interface_use_cases.leaderboard.LeaderboardInteractor;
import application.interface_use_cases.login.LoginInteractor;
import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import application.interface_use_cases.player_data.PlayerDataAccessInterface;
import application.interface_use_cases.player_data.SavePlayerDataInteractor;
import application.interface_use_cases.register.RegisterInteractor;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import domain.player.PlayerSession;
import interface_adapter.Leaderboard.LeaderboardController;
import interface_adapter.Leaderboard.LeaderboardPresenter;
import interface_adapter.Leaderboard.LeaderboardViewModel;
import interface_adapter.login.*;
import interface_adapter.register.*;

public class ViewFactory {

    private static FirebaseLoginRegisterDataAccess firebaseAuth;
    private static LoadPlayerDataInteractor loadPlayer;
    private static PlayerSession currentSession;
    private static PlayerDataAccessInterface playerDataAccess;

    public static void init(FirebaseLoginRegisterDataAccess auth,
                            LoadPlayerDataInteractor loader,PlayerDataAccessInterface dataAccess) {

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
        return new LeaderboardView(vm,controller, vmLeader);
    }


    public static GameScreen createGameScreen() {
        SavePlayerDataInteractor saveScore = new SavePlayerDataInteractor(playerDataAccess,currentSession);
        return new GameScreen(currentSession,saveScore);
    }
    public static PlayerSession getCurrentSession() {
        return currentSession;
    }
    public static PlayerDataAccessInterface getPlayerDataAccess() {
        return playerDataAccess;
    }

    public static void setCurrentSession(PlayerSession session) {
        currentSession = session;
    }
}
