package application.account_features.player_data;

import domain.player.PlayerData;
import domain.player.PlayerSession;
import java.util.List;

/**
 * Data access interface for retrieving and saving player session data
 */

public interface PlayerDataAccessInterface {

    /**
     * Load the stored player session associated with given user
     *
     * @param email
     * @param uid
     * @return
     */
    PlayerSession loadPlayerData(String email, String uid);

    /**
     * Saves the player's session back to persistent storage
     *
     * @param session
     */
    void savePlayerData(PlayerSession session);

    List<PlayerData> getAllPlayers();
}
