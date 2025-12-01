package application.game_features.update_entity;

import domain.GamePosition;
import domain.entities.Train;
import domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TrainBehaviourTest {

    private TrainBehaviour behaviour;

    private Train mockTrain;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        behaviour = new TrainBehaviour(mockPlayer);
        mockTrain = mock(Train.class);

        when(mockTrain.getPosition()).thenReturn(new GamePosition(0,0,0));
        when(mockTrain.getRideOffset()).thenReturn(new GamePosition(0, 2, 0));
        when(mockTrain.getSpeed()).thenReturn(10);
    }

    @Test
    @DisplayName("Should not move if throttle is zero")
    void shouldNotMoveWithoutThrottle() {
        when(mockTrain.getThrottle()).thenReturn(0.0f);

        behaviour.execute(new EntityBehaviourInputData(mockTrain, 1f));

        verify(mockTrain, never()).setPosition(any());
    }

    @Test
    @DisplayName("Should move train based on speed and throttle")
    void shouldMoveTrain() {
        when(mockTrain.getThrottle()).thenReturn(1.0f);

        ArgumentCaptor<GamePosition> posCaptor = ArgumentCaptor.forClass(GamePosition.class);

        behaviour.execute(new EntityBehaviourInputData(mockTrain, 1f));

        verify(mockTrain).setPosition(posCaptor.capture());
        GamePosition newPos = posCaptor.getValue();

        assertEquals(10.0f, newPos.x, 0.001);
    }

    @Test
    @DisplayName("Should move player if player is riding this train")
    void shouldMovePlayerIfRiding() {
        when(mockTrain.getThrottle()).thenReturn(1.0f);
        when(mockPlayer.getCurrentRide()).thenReturn(mockTrain);

        // Train moves to (10, 0, 0) and offset is (0, 2, 0)
        // Player should be at (10, 2, 0)
        ArgumentCaptor<GamePosition> playerPosCaptor = ArgumentCaptor.forClass(GamePosition.class);

        behaviour.execute(new EntityBehaviourInputData(mockTrain, 1f));

        verify(mockPlayer).setPosition(playerPosCaptor.capture());
        GamePosition playerPos = playerPosCaptor.getValue();

        assertEquals(10.0f, playerPos.x, 0.001);
        assertEquals(2.0f, playerPos.y, 0.001);
    }

    @Test
    @DisplayName("Should decay throttle over time")
    void shouldDecayThrottle() {
        when(mockTrain.getThrottle()).thenReturn(1.0f);

        behaviour.execute(new EntityBehaviourInputData(mockTrain, 1f));

        // throttle * (1f - 0.5f * deltaTime) -> 1.0 * (1 - 0.5) = 0.5
        verify(mockTrain).setThrottle(0.5f);
    }
}
