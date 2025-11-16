package data_access;

import UseCases.PlayerData.PlayerDataAccessInterface;
import domain.entities.PlayerSession;

import java.util.HashMap;
import java.util.Map;

public class MockPlayerDataAccess implements PlayerDataAccessInterface {

    private final Map<String, PlayerSession> database = new HashMap<>();

    @Override
    public PlayerSession loadPlayerData(String uid, String email) {

        if (!database.containsKey(uid)) {
            PlayerSession session = new PlayerSession();
            session.setUid(uid);
            session.setEmail(email);
            session.setHeightScore(0);
            session.setLastScore(0);
            database.put(uid, session);
        }

        return database.get(uid);
    }

    @Override
    public void savePlayerData(PlayerSession session) {
        database.put(session.getUid(), session);
    }
}
