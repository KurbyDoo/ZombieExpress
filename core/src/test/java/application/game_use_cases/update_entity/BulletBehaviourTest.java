package application.game_use_cases.update_entity;

import domain.GamePosition;
import domain.entities.Bullet;
import domain.entities.Entity;
import domain.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class BulletBehaviourTest {

    private BulletBehaviour behaviour;
    private BehaviourContext context;
    private Bullet mockBullet;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        behaviour = new BulletBehaviour();
        mockBullet = mock(Bullet.class);
        mockPlayer = mock(Player.class);

        context = new BehaviourContext(null, mockPlayer, 1.0f);

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

        behaviour.update(mockBullet, context);

        // BulletBehaviour speed = 30.0f, direction (1,0,0) → velocity (30,0,0)
        verify(mockBullet).setVelocity(30.0f, 0, 0);

        // Yaw = atan2(-x, -z) = atan2(-1, 0) in degrees
        float expectedYaw = (float) Math.toDegrees(Math.atan2(-1.0f, -0.0f));
        verify(mockBullet).setYaw(expectedYaw);
    }

    @Test
    @DisplayName("Should handle non-normalized direction")
    void shouldNormalizeDirection() {
        when(mockBullet.getDirection()).thenReturn(new GamePosition(10f, 0, 0));

        behaviour.update(mockBullet, context);

        // Normalized direction → still along X, speed = 30
        verify(mockBullet).setVelocity(30.0f, 0, 0);
    }

    @Test
    @DisplayName("Should handle vertical component ignored (y=0)")
    void shouldIgnoreVerticalDirection() {
        when(mockBullet.getDirection()).thenReturn(new GamePosition(0, 5, 0));

        behaviour.update(mockBullet, context);

        // y component is zeroed in BulletBehaviour
        verify(mockBullet).setVelocity(0.0f, 0, 0);

        float expectedYaw = (float) Math.toDegrees(Math.atan2(-0.0f, -0.0f));
        verify(mockBullet).setYaw(expectedYaw);
    }
}
