package interface_adapter.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import domain.items.Item;
import domain.items.RangedWeapon;
import domain.player.Inventory;
import domain.player.InventorySlot;
import domain.player.Player;
import domain.world.AmmoType;
import interface_adapter.controllers.ShootController;

public class ShootInputAdapter extends InputAdapter {

    private final Player player;
    private final ShootController shootController;

    public ShootInputAdapter(Player player, ShootController shootController) {
        this.player = player;
        this.shootController = shootController;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) {
            return false;
        }
        Inventory inv = player.getInventory();
        int slotIndex = player.getCurrentSlot();
        InventorySlot slot = inv.getSlot(slotIndex);

        if (slot == null || slot.isEmpty()) {
            return false;
        }

        Item held = slot.getItem();
        if (!(held instanceof RangedWeapon)) {
            return false;
        }

        RangedWeapon gun = (RangedWeapon) held;
        AmmoType ammoType = gun.getAmmoType();

        boolean hasAmmo = player.consumeAmmo(ammoType);
        if (!hasAmmo) {
            return false;
        }
        shootController.onShootKeyPressed();
        return true;
    }
}

