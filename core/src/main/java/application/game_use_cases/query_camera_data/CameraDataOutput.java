package application.game_use_cases.query_camera_data;

import domain.GamePosition;

/**
 * Data Transfer Object (DTO) used to transfer minimal, necessary data
 * from the Application layer to the Interface Adapter (Controller) for camera positioning.
 */
public class CameraDataOutput {
    private final GamePosition position;
    private final GamePosition direction;
    private final GamePosition up;

    public CameraDataOutput(GamePosition position, GamePosition direction, GamePosition up) {
        this.position = position;
        this.direction = direction;
        this.up = up;
    }

    public GamePosition getPosition() {
        return position;
    }

    public GamePosition getDirection() {
        return direction;
    }

    public GamePosition getUp() {
        return up;
    }
}

