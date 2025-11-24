package UseCases.PlayerData;
import domain.player.PlayerSession;

/**
 * Data access interface for retrieving and saving player session data
 */

public interface PlayerDataAccessInterface {

    /**
     * Load the stored player session associated with given user
     * @param uid
     * @param email
     * @return
     */
    PlayerSession loadPlayerData(String uid, String email);

    /**
     * Saves the player's session back to persistent storage
     * @param session
     */
    void savePlayerData(PlayerSession session);
    // store back the player info to the database
}
