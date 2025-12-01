package application.interface_use_cases.player_data;
import domain.player.PlayerSession;

public class SavePlayerDataInteractor {
    private final PlayerDataAccessInterface playerDataAccess;
    private final PlayerSession session;

    public SavePlayerDataInteractor(PlayerDataAccessInterface playerDataAccess,
                                    PlayerSession session) {
        this.playerDataAccess = playerDataAccess;
        this.session = session;
    }

    public void execute(SavePlayerDataInputData input){
        int newScore = input.getLastScore();

        session.setLastScore(newScore);
        session.setHeightScore(Math.max(session.getHeightScore(), newScore));
        playerDataAccess.savePlayerData(session);
        System.out.println("[SavePlayerData] New Score = " + input.getLastScore());
        System.out.println("[SavePlayerData] Session AFTER update: last="
            + session.getLastScore()
            + " top=" + session.getHeightScore());
    }

}
