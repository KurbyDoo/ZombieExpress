package presentation.view.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import domain.entities.Player;

public class AmmoHudElement implements HudElement {

    private final Player player;
    private final Label ammoLabel;

    public AmmoHudElement(Stage stage, Label.LabelStyle style, Player player) {
        this.player = player;

        ammoLabel = new Label("", style);

        Table table = new Table();
        table.setFillParent(true);
        table.bottom().left().padLeft(10).padBottom(105);
        table.add(ammoLabel);

        stage.addActor(table);
    }

    @Override
    public void update(float deltaTime) {
        int pistol = player.getPistolAmmo();
        int rifle  = player.getRifleAmmo();
        ammoLabel.setText(
            String.format("Pistol Ammo: %d\nRifle Ammo: %d", pistol, rifle)
        );
    }
}
