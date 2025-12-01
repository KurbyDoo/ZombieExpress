package infrastructure.input_boundary;
package infrastructure.input_boundary;
/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Infrastructure)
 *
 * DESIGN PATTERNS:
 * - Adapter Pattern: Adapts LibGDX input to use case InputBoundary.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] Correct layer for LibGDX code.
 * - [PASS] Uses InputBoundary abstraction.
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import presentation.controllers.ShootController;

public class ShootInputAdapter extends InputAdapter {

    private final ShootController shootController;

    public ShootInputAdapter(ShootController shootController) {
        this.shootController = shootController;
    }


    public void pollInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            shootController.onShootKeyPressed();
        }
    }
}
