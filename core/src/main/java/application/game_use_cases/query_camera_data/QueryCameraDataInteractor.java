package application.game_use_cases.query_camera_data;

import domain.player.Player;
import domain.GamePosition;

/**
 * Use Case responsible for querying the correct position and direction
 * that the camera should follow, using the domain's GamePosition.
 */
public class QueryCameraDataInteractor implements QueryCameraDataInputBoundary {

    // Dependency on the Domain entity (Player)
    private final Player player;

    // NOTE: Assumes Player methods (like getPosition) return domain.GamePosition
    public QueryCameraDataInteractor(Player player) {
        this.player = player;
    }

    /**
     * Executes the query. Delegates position and direction retrieval to the Player entity.
     * @return CameraDataOutput containing the tracked position and direction.
     */
    @Override
    public CameraDataOutput execute() {
        // We call methods on the Player entity that now return GamePosition
        GamePosition trackedPosition = player.getPosition();
        GamePosition direction = player.getDirection();
        GamePosition up = player.getUp();

        // Package the domain objects into the Application DTO
        return new CameraDataOutput(trackedPosition, direction, up);
    }
}

