package application.game_features.query_camera_data_test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import application.game_features.query_camera_data.CameraDataOutput;
import application.game_features.query_camera_data.PlayerCameraDataQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

// NOTE: These are required imports for the dependencies of the Adapter
import domain.world.GamePosition;
import domain.player.Player;


/**
 * Unit tests for the PlayerCameraDataQuery Adapter.
 * This ensures the adapter correctly fetches the three camera vectors
 * (Position, Direction, Up) from the Player and maps them to the CameraDataOutput DTO.
 */
class TestPlayerCameraDataQuery {

    private PlayerCameraDataQuery queryAdapter;

    private Player mockPlayer;

    private GamePosition testPosition;
    private GamePosition testDirection;
    private GamePosition testUp;


    /*
        Sets up the Mock Player and initializes the Adapter (Constructor test coverage)
     */
    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);

        testPosition = new GamePosition(10.0f, 5.0f, 2.0f);
        testDirection = new GamePosition(0.5f, 0.0f, -0.8f);
        testUp = new GamePosition(0.0f, 1.0f, 0.0f);

        queryAdapter = new PlayerCameraDataQuery(mockPlayer);
    }

    @Nested
    @DisplayName("Test Successful Query and Mapping")
    class SuccessfulQueryTests {

        @BeforeEach
        void setUpMockBehavior() {
            when(mockPlayer.getPosition()).thenReturn(testPosition);
            when(mockPlayer.getDirection()).thenReturn(testDirection);
            when(mockPlayer.getUp()).thenReturn(testUp);
        }

        @Test
        @DisplayName("Flow succeeds and maps all player vectors correctly")
        void shouldReturnCorrectlyMappedVectors() {
            CameraDataOutput output = queryAdapter.execute();
            assertNotNull(output, "Output DTO should not be null.");

            assertEquals(testPosition, output.getPosition(),
                "The position vector must match the position retrieved from the player.");

            assertEquals(testDirection, output.getDirection(),
                "The direction vector must match the direction retrieved from the player.");


            assertEquals(testUp, output.getUpwards(),
                "The up vector must match the up vector retrieved from the player.");
        }

        @Test
        @DisplayName("Flow handles identity vectors correctly (Edge Case)")
        void shouldHandleIdentityVectors() {
            GamePosition zeroPos = new GamePosition(0.0f, 0.0f, 0.0f);
            GamePosition defaultDir = new GamePosition(0.0f, 0.0f, -1.0f);
            GamePosition defaultUp = new GamePosition(0.0f, 1.0f, 0.0f);

            when(mockPlayer.getPosition()).thenReturn(zeroPos);
            when(mockPlayer.getDirection()).thenReturn(defaultDir);
            when(mockPlayer.getUp()).thenReturn(defaultUp);

            CameraDataOutput output = queryAdapter.execute();

            assertEquals(zeroPos, output.getPosition(), "Should map zero position correctly.");
            assertEquals(defaultDir, output.getDirection(), "Should map default direction correctly.");
            assertEquals(defaultUp, output.getUpwards(), "Should map default up vector correctly.");
        }
    }
}
