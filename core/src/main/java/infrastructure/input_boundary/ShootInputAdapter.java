package infrastructure.input_boundary;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import domain.items.Item;
import domain.items.RangedWeapon;
import domain.player.Inventory;
import domain.player.InventorySlot;
import domain.player.Player;
import presentation.controllers.ShootController;

public class ShootInputAdapter extends InputAdapter {

    private final Player player;
    private final ShootController shootController;

    public ShootInputAdapter(Player player, ShootController shootController) {
        this.player = player;
        this.shootController = shootController;
    }

@Override
public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (button == Input.Buttons.LEFT) {
        Inventory inv = player.getInventory();
        int slotIndex = player.getCurrentSlot();
        InventorySlot slot = inv.getSlot(slotIndex);

        if (slot != null && !slot.isEmpty()) {
            Item held = slot.getItem();
            if (held instanceof RangedWeapon) {
                shootController.onShootKeyPressed();
                return true;
            }
        }
        return false;
    }
    return false;
}
}
