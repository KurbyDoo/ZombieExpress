package presentation.view.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import domain.player.InventorySlot;
import domain.player.Player;

public class HeldItemHudElement implements HudElement {

    private final Player player;
    private final Image heldItemImage;

    public HeldItemHudElement(Stage uiStage, Player player) {
        this.player = player;

        heldItemImage = new Image();

        Table table = new Table();
        table.setFillParent(true);
        table.bottom().right();
        table.add(heldItemImage).size(384); // tweak size

        uiStage.addActor(table);
    }

    @Override
    public void update(float deltaTime) {
        InventorySlot slot = player.getInventory().getSlot(player.getCurrentSlot());
        if (slot == null || slot.isEmpty()) {
            heldItemImage.setDrawable(null);
            return;
        }

        Drawable drawable = ItemImageFactory.getImageForItem(slot.getItem());
        heldItemImage.setDrawable(drawable);
    }
}
