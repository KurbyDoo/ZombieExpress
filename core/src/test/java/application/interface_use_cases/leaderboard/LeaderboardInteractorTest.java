package application.interface_use_cases.leaderboard;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import application.account_features.leaderboard.LeaderboardInteractor;
import application.account_features.leaderboard.LeaderboardOutputBoundary;
import application.account_features.player_data.PlayerDataAccessInterface;
import domain.player.PlayerData;
import java.util.Arrays;
import java.util.List;

public class LeaderboardInteractorTest {

    @Test
    public void testLeaderboardSortsPlayersCorrectly() {

        PlayerDataAccessInterface mockAccess = mock(PlayerDataAccessInterface.class);
        PlayerData p1 = new PlayerData("1", "a@mail.com", 30, 100);
        PlayerData p2 = new PlayerData("2", "b@mail.com", 50, 300);
        PlayerData p3 = new PlayerData("3", "c@mail.com", 10, 150);

        List<PlayerData> fakeList = Arrays.asList(p1, p2, p3);
        when(mockAccess.getAllPlayers()).thenReturn(fakeList);
        LeaderboardOutputBoundary mockPresenter = mock(LeaderboardOutputBoundary.class);
        LeaderboardInteractor interactor = new LeaderboardInteractor(mockAccess, mockPresenter);
        interactor.loadLeaderboard();
        Mockito.verify(mockPresenter).present(Mockito.argThat(data -> {

            List<PlayerData> top = data.getPlayers();
            return top.get(0).getHighScore() == 300 &&
                top.get(1).getHighScore() == 150 &&
                top.get(2).getHighScore() == 100;

        }));
    }

    @Test
    public void testLeaderboardLimitsToTop10() {

        PlayerDataAccessInterface mockAccess = mock(PlayerDataAccessInterface.class);

        List<PlayerData> fakeList = Arrays.asList(
            new PlayerData("1", "p1@mail.com", 0, 10),
            new PlayerData("2", "p2@mail.com", 0, 20),
            new PlayerData("3", "p3@mail.com", 0, 30),
            new PlayerData("4", "p4@mail.com", 0, 40),
            new PlayerData("5", "p5@mail.com", 0, 50),
            new PlayerData("6", "p6@mail.com", 0, 60),
            new PlayerData("7", "p7@mail.com", 0, 70),
            new PlayerData("8", "p8@mail.com", 0, 80),
            new PlayerData("9", "p9@mail.com", 0, 90),
            new PlayerData("10", "p10@mail.com", 0, 100),
            new PlayerData("11", "p11@mail.com", 0, 110),
            new PlayerData("12", "p12@mail.com", 0, 120),
            new PlayerData("13", "p13@mail.com", 0, 130),
            new PlayerData("14", "p14@mail.com", 0, 140),
            new PlayerData("15", "p15@mail.com", 0, 150)
        );

        when(mockAccess.getAllPlayers()).thenReturn(fakeList);

        LeaderboardOutputBoundary mockPresenter = mock(LeaderboardOutputBoundary.class);
        LeaderboardInteractor interactor = new LeaderboardInteractor(mockAccess, mockPresenter);

        interactor.loadLeaderboard();

        Mockito.verify(mockPresenter).present(Mockito.argThat(data -> {
            return data.getPlayers().size() == 10;
        }));
    }
}
