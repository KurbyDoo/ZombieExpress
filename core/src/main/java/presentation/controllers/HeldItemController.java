package presentation.controllers;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import domain.entities.InventorySlot;
import domain.entities.Item;
import domain.entities.Player;
import infrastructure.rendering.ObjectRenderer;
import infrastructure.rendering.ItemPickupSceneFactory;
import net.mgsx.gltf.scene3d.scene.Scene;

public class HeldItemController {

    private final Player player;
    private final ObjectRenderer renderer;

    private Scene heldScene;     // current 3D scene in hand
    private Item currentItem;    // which item we are currently holding

    public HeldItemController(Player player, ObjectRenderer renderer) {
        this.player = player;
        this.renderer = renderer;
    }

    public void updateHeldItem() {
        // 1. Determine the item in the currently selected slot
        InventorySlot slot = player.getInventory().getSlot(player.getCurrentSlot());
        Item newItem = (slot == null || slot.isEmpty()) ? null : slot.getItem();

        // 2. If nothing changed, just update position relative to camera
        if (newItem == currentItem) {
            updateHeldTransform();
            return;
        }

        // 3. If the item changed, remove old scene
        if (heldScene != null) {
            renderer.removeScene(heldScene);
            heldScene = null;
        }
        currentItem = newItem;

        // 4. No item -> nothing to render in hand
        if (currentItem == null) {
            return;
        }

        // 5. Create new scene for the currently held item
        heldScene = ItemPickupSceneFactory.createHeldSceneForItem(currentItem);
        renderer.addToSceneManager(heldScene);

        // 6. Position it correctly once
        updateHeldTransform();
    }

    private void updateHeldTransform() {
        if (heldScene == null || currentItem == null) return;

        PerspectiveCamera cam = renderer.camera;

        // Camera basis vectors
        Vector3 dir   = new Vector3(cam.direction).nor(); // forward
        Vector3 up    = new Vector3(cam.up).nor();        // up
        Vector3 right = new Vector3(dir).crs(up).nor();   // right

        // Offsets to position item relative to camera
        float forwardOffset = 0.7f;   // how far in front
        float rightOffset   = 0.6f;   // how far to the right
        float downOffset    = -0.5f;  // negative = lower

        Vector3 handPos = new Vector3(cam.position)
            .add(new Vector3(dir).scl(forwardOffset))
            .add(new Vector3(right).scl(rightOffset))
            .add(new Vector3(up).scl(downOffset));

        // Get scale for this item (use your helper or duplicate switch)
        float scale = ItemPickupSceneFactory.getHeldScaleForItemName(currentItem.getName());

        Matrix4 transform = heldScene.modelInstance.transform;

        // Build a full transform from camera basis + hand position
        // X-axis = right, Y-axis = up, Z-axis = forward (dir)
        transform.idt();
        transform.set(right, up, dir, handPos);
        transform.scale(scale, scale, scale);
    }
}
