package application.game_features.update_entity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import domain.entities.Bullet;
import domain.player.Player;
import domain.world.GamePosition;

class BulletBehaviourTest {

    private BulletBehaviour behaviour;
    private Bullet mockBullet;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        behaviour = new BulletBehaviour();
        mockBullet = mock(Bullet.class);
        mockPlayer = mock(Player.class);

        // Start positions
        //when(mockBullet.getPosition()).thenReturn(new GamePosition(0, 0, 0));

//        GamePosition bulletPos = new GamePosition(0, 0, 0);
//        GamePosition bulletDir = new GamePosition(0, 0, 0);
//        mockBullet = new Bullet(1, bulletPos, bulletDir, true);
    }

    @Test
    @DisplayName("Should set velocity based on bullet direction")
    void shouldMoveInBulletDirection() {

        when(mockBullet.getDirection()).thenReturn(new GamePosition(10f, 0, 0));

        behaviour.execute(new EntityBehaviourInputData(mockBullet, 1f));

        // BulletBehaviour speed = 30.0f, direction (1,0,0) → velocity (30,0,0)
        verify(mockBullet).setVelocity(90.0f, 0, 0);

        // Yaw = atan2(-x, -z) = atan2(-1, 0) in degrees
        float expectedYaw = (float) Math.toDegrees(Math.atan2(-1.0f, -0.0f));
        verify(mockBullet).setYaw(expectedYaw);
    }

    @Test
    @DisplayName("Should handle non-normalized direction")
    void shouldNormalizeDirection() {
        when(mockBullet.getDirection()).thenReturn(new GamePosition(10f, 0, 0));

        behaviour.execute(new EntityBehaviourInputData(mockBullet, 1f));

        // Normalized direction → still along X, speed = 30
        verify(mockBullet).setVelocity(90.0f, 0, 0);
    }

    @Test
    @DisplayName("Should handle vertical component ignored (y=0)")
    void shouldIgnoreVerticalDirection() {
        when(mockBullet.getDirection()).thenReturn(new GamePosition(0, 5, 0));

        behaviour.execute(new EntityBehaviourInputData(mockBullet, 1f));

        // y component is zeroed in BulletBehaviour
        verify(mockBullet).setVelocity(0.0f, 0, 0);

        float expectedYaw = (float) Math.toDegrees(Math.atan2(-0.0f, -0.0f));
        verify(mockBullet).setYaw(expectedYaw);
    }
}
