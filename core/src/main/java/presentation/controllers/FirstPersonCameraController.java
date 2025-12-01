/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Presentation/Controllers)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package presentation.controllers;

import domain.player.Player;
import com.badlogic.gdx.graphics.Camera;

public class FirstPersonCameraController extends CameraController {
    public FirstPersonCameraController(Camera camera, Player player) {
        super(camera, player);
    }
}
