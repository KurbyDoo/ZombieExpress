package interface_adapter.controllers;

import application.game_use_cases.query_camera_data.CameraDataOutput;
import application.game_use_cases.query_camera_data.QueryCameraDataInputBoundary;
import domain.GamePosition;
import com.badlogic.gdx.graphics.Camera;


/**
 * Interface Adapter responsible for controlling and interpolating the camera's view.
 * It is decoupled from the Player entity, relying only on the Application Layer contract.
 * All state management and interpolation logic is handled using the Domain's GamePosition.
 */
public class CameraController {
    private final Camera camera;
    private final QueryCameraDataInputBoundary cameraDataQuery;

    private final GamePosition previousPosition;
    private final GamePosition previousDirection;
    private final GamePosition previousUp;

    public CameraController(Camera camera, QueryCameraDataInputBoundary cameraDataQuery) {
        this.camera = camera;
        this.cameraDataQuery = cameraDataQuery;

        previousPosition = new GamePosition();
        previousDirection = new GamePosition();
        previousUp = new GamePosition();

        updatePrevious();
    }

    public void updatePrevious() {
        CameraDataOutput currentData = cameraDataQuery.execute();

        previousPosition.set(currentData.getPosition());
        previousDirection.set(currentData.getDirection());
        previousUp.set(currentData.getUp());
    }


    public void renderCamera(float alpha) {
        CameraDataOutput currentData = cameraDataQuery.execute();

        GamePosition currentPos = currentData.getPosition();
        GamePosition currentDir = currentData.getDirection();
        GamePosition currentUp = currentData.getUp();


        GamePosition newPos = new GamePosition(previousPosition).lerp(currentPos, alpha);
        GamePosition newDir = new GamePosition(previousDirection).lerp(currentDir, alpha);
        GamePosition newUp = new GamePosition(previousUp).lerp(currentUp, alpha);

        camera.position.set(newPos.x, newPos.y, newPos.z);
        camera.direction.set(newDir.x, newDir.y, newDir.z);
        camera.up.set(newUp.x, newUp.y, newUp.z);
        camera.update();
    }
}
