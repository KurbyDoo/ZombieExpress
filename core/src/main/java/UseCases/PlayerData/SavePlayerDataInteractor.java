package UseCases.PlayerData;

import domain.entities.PlayerSession;

public class SavePlayerDataInteractor {
    private final PlayerDataAccessInterface dataAccess;

    public SavePlayerDataInteractor(PlayerDataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void save(PlayerSession playerSession) {
        dataAccess.savePlayerData(playerSession);
    }

}
