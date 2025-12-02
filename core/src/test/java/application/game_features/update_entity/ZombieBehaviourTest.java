package application.game_features.update_entity;

import static org.mockito.Mockito.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import domain.entities.Entity;
import domain.player.Player;
import domain.world.GamePosition;

class ZombieBehaviourTest {

    private ZombieBehaviour behaviour;
    private Entity mockZombie;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        behaviour = new ZombieBehaviour(mockPlayer);
        mockZombie = mock(Entity.class);

        // Start positions
        when(mockZombie.getPosition()).thenReturn(new GamePosition(0, 0, 0));
    }

    @Test
    @DisplayName("Should move towards player")
    void shouldMoveTowardsPlayer() {
        // Player is at (10, 0, 0)
        when(mockPlayer.getPosition()).thenReturn(new GamePosition(10, 0, 0));

        behaviour.execute(new EntityBehaviourInputData(mockZombie, 1f));

        // Zombie speed is 3.0f
        // Direction is (1,0,0)
        // Velocity should be (3, 0, 0).
        verify(mockZombie).setVelocity(3.0f, 0, 0);
    }

    @Test
    @DisplayName("Should set rotation (yaw) facing the player")
    void shouldFacePlayer() {
        when(mockPlayer.getPosition()).thenReturn(new GamePosition(10, 0, 10));

        behaviour.execute(new EntityBehaviourInputData(mockZombie, 1f));

        // Verify setYaw is called.
        verify(mockZombie).setYaw(anyFloat());
    }

    @Test
    @DisplayName("Should ignore Y axis (height) differences for velocity")
    void shouldIgnoreHeightDiff() {
        // Player is high up, but on same XZ plane
        when(mockPlayer.getPosition()).thenReturn(new GamePosition(10, 50, 0));

        behaviour.execute(new EntityBehaviourInputData(mockZombie, 1f));

        // Should still move purely along X axis, Y velocity should be 0
        verify(mockZombie).setVelocity(3.0f, 0, 0);
    }
}
