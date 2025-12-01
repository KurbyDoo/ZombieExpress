package interface_adapter.view.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import interface_adapter.controllers.PickupController;

public class PickupPromptHudElement implements HudElement {

    private final PickupController controller;
    private final Label promptLabel;

    public PickupPromptHudElement(Stage stage, Label.LabelStyle style, PickupController controller) {
        this.controller = controller;

        promptLabel = new Label("", style);

        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(150);
        table.add(promptLabel);

        stage.addActor(table);
    }

    @Override
    public void update(float deltaTime) {
        String message = controller.getCurrentPickupMessage();
        if (message == null) {
            promptLabel.setText("");
        } else {
            promptLabel.setText(message);
        }
    }
}
