package application.interface_use_cases.player_data;
import domain.player.PlayerSession;

/**
 * Data access interface for retrieving and saving player session data
 */

public interface PlayerDataAccessInterface {

    /**
     * Load the stored player session associated with given user
     * @param email
     * @param uid
     * @return
     */
    PlayerSession loadPlayerData(String email,String uid);

    /**
     * Saves the player's session back to persistent storage
     * @param session
     */
    void savePlayerData(PlayerSession session);
    // store back the player info to the database
}
