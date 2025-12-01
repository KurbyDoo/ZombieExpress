package application.game_use_cases.update_entity;

import domain.GamePosition;
import domain.entities.Bullet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BulletBehaviourTest {

    private BulletBehaviour behaviour;
    private Bullet bullet;
    private EntityBehaviourInputData inputData;

    @BeforeEach
    void setUp() {
        // Initial position and direction
        GamePosition bulletPos = new GamePosition(0, 0, 0);
        GamePosition bulletDir = new GamePosition(10, 0, 0); // Points along +X
        bullet = new Bullet(1, bulletPos, bulletDir, true);
        inputData = new EntityBehaviourInputData(bullet, 1f);

        behaviour = new BulletBehaviour();
    }

    @Test
    @DisplayName("Should set velocity based on bullet direction")
    void shouldMoveInBulletDirection() {
        behaviour.execute(inputData);

        // BulletBehaviour speed = 30.0f, direction (1,0,0) → velocity (30,0,0)
        assertEquals(30.0f, bullet.getVelocity().x, 0.001f);
        assertEquals(0.0f, bullet.getVelocity().y, 0.001f);
        assertEquals(0.0f, bullet.getVelocity().z, 0.001f);

        // Yaw = atan2(-x, -z) = atan2(-1, 0) in degrees
        float expectedYaw = (float) Math.toDegrees(Math.atan2(-1.0f, -0.0f));
        assertEquals(expectedYaw, bullet.getYaw(), 0.001f);
    }

    @Test
    @DisplayName("Should handle non-normalized direction")
    void shouldNormalizeDirection() {
        // Use a non-normalized vector
        bullet.setDirection(new GamePosition(10, 0, 0));

        behaviour.execute(inputData);

        // Normalized direction → along X, speed = 30
        assertEquals(30.0f, bullet.getVelocity().x, 0.001f);
        assertEquals(0.0f, bullet.getVelocity().y, 0.001f);
        assertEquals(0.0f, bullet.getVelocity().z, 0.001f);
    }

    @Test
    @DisplayName("Should ignore vertical component (y=0)")
    void shouldIgnoreVerticalDirection() {
        bullet.setDirection(new GamePosition(0, 5, 0)); // vertical direction

        behaviour.execute(inputData);

        // y component is zeroed in BulletBehaviour
        assertEquals(0.0f, bullet.getVelocity().x, 0.001f);
        assertEquals(0.0f, bullet.getVelocity().y, 0.001f);
        assertEquals(0.0f, bullet.getVelocity().z, 0.001f);

        // Yaw should handle zero direction safely
        float expectedYaw = (float) Math.toDegrees(Math.atan2(-0.0f, -0.0f));
        assertEquals(expectedYaw, bullet.getYaw(), 0.001f);
    }
}
