package application.interface_use_cases.player_data;

import domain.player.PlayerData;
import domain.player.PlayerSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class MockPlayerDataAccess implements PlayerDataAccessInterface {
    String recievedEmail;
    String recievedUid;
    PlayerSession returnedSession;

    @Override
    public PlayerSession loadPlayerData(String email, String uid) {
        this.recievedEmail = email;
        this.recievedUid = uid;
        return returnedSession;
    }

    @Override
    public void savePlayerData(PlayerSession playerSession) {
    }
    @Override
    public List<PlayerData> getAllPlayers(){
        return new ArrayList<>();
    }
}
public class LoadPlayerDataInteractorTest{
    private MockPlayerDataAccess mockDAO;
    private LoadPlayerDataInteractor interactor;

    @BeforeEach
    void setUp() {
        mockDAO = new MockPlayerDataAccess();
        interactor = new LoadPlayerDataInteractor(mockDAO);
    }

    @Test
    void loadShouldCallDAOAndReturnSession() {
        PlayerSession mock = new PlayerSession();
        mock.setEmail("mail@mail.com");
        mock.setUid("thisistheuid");
        mock.setHeightScore(100);
        mock.setLastScore(10);

        mockDAO.returnedSession = mock;
        PlayerSession result = interactor.load("mail@mail.com", "thisistheuid");

        assertEquals("mail@mail.com",mockDAO.recievedEmail);
        assertEquals("thisistheuid",mockDAO.recievedUid);
        assertSame(mock,result);
    }
}
