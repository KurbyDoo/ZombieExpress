package application.game_features.update_entity;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import domain.entities.Entity;
import domain.entities.PlayerEntity;
import domain.player.Player;
import domain.world.GamePosition;

class PlayerEntityBehaviourTest {

    private PlayerEntityBehaviour behaviour;

    private Player mockPlayer;
    private PlayerEntity mockPlayerEntity;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);

        when(mockPlayer.getPosition()).thenReturn(new GamePosition(0, 0, 0));

        behaviour = new PlayerEntityBehaviour(mockPlayer);
        mockPlayerEntity = mock(PlayerEntity.class);
    }

    @Test
    @DisplayName("Should ignore entities that are not instances of PlayerEntity")
    void shouldIgnoreNonPlayerEntities() {
        Entity mockGenericEntity = mock(Entity.class);

        behaviour.execute(new EntityBehaviourInputData(mockGenericEntity, 1f));

        verify(mockGenericEntity, never()).setPosition(any());
        verify(mockPlayer, never()).setCurrentHealth(any(Float.class));
    }

    @Test
    @DisplayName("Should sync PlayerEntity position to Player position")
    void shouldUpdatePlayerEntityPosition() {
        GamePosition expectedPosition = new GamePosition(100, 50, 0);
        when(mockPlayer.getPosition()).thenReturn(expectedPosition);

        behaviour.execute(new EntityBehaviourInputData(mockPlayerEntity, 1f));

        verify(mockPlayerEntity).setPosition(expectedPosition);
    }

    @Test
    @DisplayName("Should sync Player health from PlayerEntity health")
    void shouldUpdatePlayerHealth() {
        float currentEntityHealth = 75.0f;
        when(mockPlayerEntity.getHealth()).thenReturn(currentEntityHealth);

        behaviour.execute(new EntityBehaviourInputData(mockPlayerEntity, 1f));

        verify(mockPlayer).setCurrentHealth(currentEntityHealth);
    }
}
