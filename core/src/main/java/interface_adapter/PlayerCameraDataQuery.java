package interface_adapter;

import application.game_use_cases.query_camera_data.CameraDataOutput;
import application.game_use_cases.query_camera_data.QueryCameraDataInputBoundary;
import domain.player.Player;

/**
 * Concrete implementation of the QueryCameraDataInputBoundary.
 * This class fetches the current position, direction, and up vector directly
 * from the Player entity and wraps it in the required CameraDataOutput DTO.
 */
public class PlayerCameraDataQuery implements QueryCameraDataInputBoundary {

    private final Player player;

    public PlayerCameraDataQuery(Player player) {
        this.player = player;
    }

    /**
     * Executes the query to get the current camera data.
     * @return A CameraDataOutput object containing the player's current GamePosition fields.
     */
    @Override
    public CameraDataOutput execute() {
        // The Player object implicitly holds the current camera state
        // (position, direction, and up vector).
        return new CameraDataOutput(
            player.getPosition(),
            player.getDirection(),
            player.getUp()
        );
    }
}
