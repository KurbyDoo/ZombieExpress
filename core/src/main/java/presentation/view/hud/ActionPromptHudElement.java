package presentation.view.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import presentation.controllers.ItemInteractionController;

public class ActionPromptHudElement implements HudElement {

    private final ItemInteractionController controller;
    private final Label promptLabel;

    public ActionPromptHudElement(Stage stage, Label.LabelStyle style, ItemInteractionController controller) {
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
