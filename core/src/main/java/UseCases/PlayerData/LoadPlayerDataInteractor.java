package UseCases.PlayerData;

/**
 * Interactor for loading player data use case
 */
import domain.entities.PlayerSession;

public class LoadPlayerDataInteractor {

    private final PlayerDataAccessInterface dataAccess;

    /**
     * Constructs the interactor with its required data access dependency
     * @param dataAccess
     */
    public LoadPlayerDataInteractor(PlayerDataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * loads the player's stored session
     * For this moments. only contains email, uid, highest score, latest score
     * @param email
     * @param uid
     * @return
     */
    public PlayerSession load(String email, String uid) {
        return dataAccess.loadPlayerData(email, uid);

    }
}
