package interface_adapter.controllers;

import domain.GamePosition;
import domain.player.Player;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class CameraController {
    private final Camera camera;
    private final Player player;

    private final GamePosition previousPosition;
    private final GamePosition previousDirection;
    private final GamePosition previousUp;


    public CameraController(Camera camera, Player player) {
        this.camera = camera;
        this.player = player;

        previousPosition = new GamePosition();
        previousDirection = new GamePosition();
        previousUp = new GamePosition();
    }

    public void updatePrevious() {
        previousPosition.set(player.getPosition());
        previousDirection.set(player.getDirection());
        previousUp.set(player.getUp());
    }


    public void renderCamera(float alpha) {
        GamePosition newPos = previousPosition.lerp(player.getPosition(), alpha);
        GamePosition newDir = previousDirection.lerp(player.getDirection(), alpha);
        GamePosition newUp = previousUp.lerp(player.getUp(), alpha);
        camera.position.set(newPos.x, newPos.y, newPos.z);
        camera.direction.set(newDir.x, newDir.y, newDir.z);
        camera.up.set(newUp.x, newUp.y, newUp.z);
        camera.update();
    }
}
