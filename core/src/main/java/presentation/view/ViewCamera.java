/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Presentation/Views)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package presentation.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class ViewCamera extends PerspectiveCamera {
    public ViewCamera() {
        super(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.near = 0.5f;
        this.far = 200f;
        this.update();
    }
}
