/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Presentation/HUD)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package presentation.view.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import domain.GamePosition;
import domain.player.Player;

public class DistanceHudElement implements HudElement {

    private final Player player;
    private final Label distanceLabel;

    public DistanceHudElement(Stage stage, Label.LabelStyle style, Player player) {
        this.player = player;

        distanceLabel = new Label("", style);

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(10);
        table.add(distanceLabel).center();

        stage.addActor(table);
    }

    @Override
    public void update(float deltaTime) {
        GamePosition current = player.getPosition();
        GamePosition start   = player.getStartingPosition();
        int distance = Math.max(0, (int) (current.x - start.x));
        distanceLabel.setText(String.format("Distance: %d m", distance));
    }
}
