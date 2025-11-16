package UseCases.PlayerData;

import domain.entities.PlayerSession;

public class LoadPlayerDataInteractor {

    private final PlayerDataAccessInterface dataAccess;

    public LoadPlayerDataInteractor(PlayerDataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public PlayerSession load(String uid, String email) {
        return dataAccess.loadPlayerData(uid, email);

    }
}
