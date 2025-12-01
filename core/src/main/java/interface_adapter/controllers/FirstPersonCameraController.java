package interface_adapter.controllers;

import domain.player.Player;
import com.badlogic.gdx.graphics.Camera;

public class FirstPersonCameraController extends CameraController {
    public FirstPersonCameraController(Camera camera, Player player) {
        super(camera, player);
    }
}
