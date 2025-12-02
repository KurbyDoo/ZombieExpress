package application.interface_use_cases.player_data;

import domain.player.PlayerSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockSaveDAO implements PlayerDataAccessInterface{
    PlayerSession savedSession = null;
    boolean saveCalled = false;

    @Override
    public PlayerSession loadPlayerData(String email, String uid){
        return null;
    }

    @Override
    public void savePlayerData(PlayerSession playerSession) {
        saveCalled = true;
        savedSession = playerSession;
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
        interactor = new SavePlayerDataInteractor(mockDAO,session);
    }

    @Test
    void shouldUpdateScoresAndCallSave(){
        SavePlayerDataInputData inputData = new SavePlayerDataInputData(80);
        interactor.execute(inputData);

        assertEquals(80,session.getLastScore());
        assertEquals(80,session.getHeightScore());
        assertTrue(mockDAO.saveCalled);
        assertSame(session,mockDAO.savedSession);
    }

    @Test
    void shouldNotLowerHighestScores(){
        SavePlayerDataInputData inputData = new SavePlayerDataInputData(30);
        interactor.execute(inputData);
        assertEquals(30,session.getLastScore());
        assertEquals(50,session.getHeightScore());
    }
}
