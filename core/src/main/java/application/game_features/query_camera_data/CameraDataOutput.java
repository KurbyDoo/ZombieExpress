package application.game_features.query_camera_data;

import domain.world.GamePosition;

/**
 * Data Transfer Object (DTO) used to transfer minimal, necessary data
 * from the Application layer to the Interface Adapter (Controller) for camera positioning.
 */
public class CameraDataOutput {
    private final GamePosition position;
    private final GamePosition direction;
    private final GamePosition upwards;

    public CameraDataOutput(GamePosition position, GamePosition direction, GamePosition upwards) {
        this.position = position;
        this.direction = direction;
        this.upwards = upwards;
    }

    public GamePosition getPosition() {
        return position;
    }

    public GamePosition getDirection() {
        return direction;
    }

    public GamePosition getUpwards() {
        return upwards;
    }
}

