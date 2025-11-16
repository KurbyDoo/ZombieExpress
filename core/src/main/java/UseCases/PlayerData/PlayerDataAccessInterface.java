package UseCases.PlayerData;
import domain.entities.PlayerSession;

public interface PlayerDataAccessInterface {
    PlayerSession loadPlayerData(String uid, String email);

    void savePlayerData(PlayerSession session);
    // store back the player info to the database
}
