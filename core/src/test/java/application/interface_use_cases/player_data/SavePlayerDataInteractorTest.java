package application.interface_use_cases.player_data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.account_features.player_data.PlayerDataAccessInterface;
import application.account_features.player_data.SavePlayerDataInputData;
import application.account_features.player_data.SavePlayerDataInteractor;
import domain.player.PlayerData;
import domain.player.PlayerSession;
import java.util.ArrayList;
import java.util.List;

class MockSaveDAO implements PlayerDataAccessInterface {
    PlayerSession savedSession = null;
    boolean saveCalled = false;

    @Override
    public PlayerSession loadPlayerData(String email, String uid) {
        return null;
    }

    @Override
    public void savePlayerData(PlayerSession playerSession) {
        saveCalled = true;
        savedSession = playerSession;
    }

    @Override
    public List<PlayerData> getAllPlayers() {
        return new ArrayList<>();
    }
}

public class SavePlayerDataInteractorTest {
    private MockSaveDAO mockDAO;
    private PlayerSession session;
    private SavePlayerDataInteractor interactor;

    @BeforeEach
    void setUp() {
        mockDAO = new MockSaveDAO();
        session = new PlayerSession();
        session.setLastScore(10);
        session.setHeightScore(50);
        interactor = new SavePlayerDataInteractor(mockDAO, session);
    }

    @Test
    void shouldUpdateScoresAndCallSave() {
        SavePlayerDataInputData inputData = new SavePlayerDataInputData(80);
        interactor.execute(inputData);

        assertEquals(80, session.getLastScore());
        assertEquals(80, session.getHeightScore());
        assertTrue(mockDAO.saveCalled);
        assertSame(session, mockDAO.savedSession);
    }

    @Test
    void shouldNotLowerHighestScores() {
        SavePlayerDataInputData inputData = new SavePlayerDataInputData(30);
        interactor.execute(inputData);
        assertEquals(30, session.getLastScore());
        assertEquals(50, session.getHeightScore());
    }
}
