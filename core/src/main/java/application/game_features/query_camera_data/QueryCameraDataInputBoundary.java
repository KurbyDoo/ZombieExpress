package application.game_features.query_camera_data;

/**
 * Defines the contract for querying the camera's required data (position/direction).
 */
public interface QueryCameraDataInputBoundary {
    /**
     * Executes the query and returns the necessary data for the camera controller.
     *
     * @return CameraDataOutput DTO.
     */
    CameraDataOutput execute();
}
