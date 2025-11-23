package presentation.view.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import domain.entities.WorldPickup;
import presentation.controllers.PickupController;

public class PickupPromptHudElement implements HudElement {

    private final PickupController controller;
    private final Label promptLabel;

    public PickupPromptHudElement(Stage stage, Label.LabelStyle style, PickupController controller) {
        this.controller = controller;

        promptLabel = new Label("", style);
        promptLabel.setVisible(false);

        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(150);
        table.add(promptLabel);

        stage.addActor(table);
    }

    @Override
    public void update(float deltaTime) {
        if (controller == null) {
            promptLabel.setVisible(false);
            return;
        }

        WorldPickup target = controller.getCurrentPickupTarget();

        if (target == null) {
            promptLabel.setVisible(false);
            return;
        }

        String itemName = target.getItem().getName();
        promptLabel.setText("Press E to pick up " + itemName);
        promptLabel.setVisible(true);
    }
}
