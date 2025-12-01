package presentation.view.hud;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import domain.items.RangedWeapon;
import domain.player.InventorySlot;
import domain.player.Player;

public class CrosshairHudElement implements HudElement {

    private final Player player;
    private final Image crosshairImage;

    public CrosshairHudElement(Stage stage, Player player) {
        this.player = player;

        int size = 16;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);

        int cx = 8;
        int cy = 8;
        pixmap.drawLine(cx, 2, cx, 13);
        pixmap.drawLine(2, cy, 13, cy);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        crosshairImage = new Image(texture);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(crosshairImage).size(size, size);

        stage.addActor(table);
        crosshairImage.setVisible(false);
    }

    @Override
    public void update(float deltaTime) {
        // Show only when holding a ranged weapon
        InventorySlot slot = player.getInventory().getSlot(player.getCurrentSlot());

        boolean show = (slot != null && !slot.isEmpty() && slot.getItem() instanceof RangedWeapon);

        crosshairImage.setVisible(show);
    }
}

